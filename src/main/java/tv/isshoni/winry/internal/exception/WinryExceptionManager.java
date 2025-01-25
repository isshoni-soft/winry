package tv.isshoni.winry.internal.exception;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.data.collection.map.BucketMap;
import institute.isshoni.araragi.data.collection.map.Maps;
import institute.isshoni.araragi.data.collection.map.SubMap;
import institute.isshoni.araragi.data.collection.map.TypeMap;
import institute.isshoni.araragi.exception.Exceptions;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.reflect.JStack;
import institute.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.context.IExceptionManager;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.api.exception.UnhandledException;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinryExceptionManager implements IExceptionManager {

    private final AraragiLogger logger;

    private final IWinryAnnotationManager annotationManager;

    private final BucketMap<Class<? extends Throwable>, Class<? extends IExceptionHandler<?>>> globalHandlers;
    private final SubMap<Method, Class<? extends Throwable>, List<ExceptionHandler>, BucketMap<Class<? extends Throwable>, ExceptionHandler>> methodHandlers;

    private final TypeMap<Class<? extends IExceptionHandler<?>>, IExceptionHandler<Throwable>> singletons;

    private final Constant<IWinryContext> context;

    public WinryExceptionManager(IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        this.logger = loggerFactory.createLogger(getClass());
        this.annotationManager = annotationManager;
        this.globalHandlers = Maps.bucket(new TypeMap<>());
        this.singletons = new TypeMap<>();
        this.methodHandlers = new SubMap<>(() -> Maps.bucket(new TypeMap<>()));
        this.context = new Constant<>();
    }

    @Override
    public void recover(Throwable throwable) {
        if (!this.globalHandlers.containsKey(throwable.getClass())) {
            this.logger.error(Exceptions.toString(throwable));
            this.logger.error("Recovered from error successfully!");
            return;
        }

        this.logger.debug("Tossing exception: " + throwable);

        try {
            recover(throwable, JStack.getParentMethod());
        } catch (NoSuchMethodException e) {
            runGlobals(throwable);
        }
    }

    @Override
    public void recover(Throwable throwable, Method context) {
        if (context == null) {
            runGlobals(throwable);
            return;
        }

        this.logger.debug("Tossing exception: " + throwable + " with method context: " + context);

        Streams.to(this.methodHandlers.getOrDefault(context).getOrDefault(throwable.getClass(), new LinkedList<>()))
                .map(eh -> {
                    if (eh.useSingleton()) {
                        return getSingleton((Class<IExceptionHandler<Throwable>>) eh.value());
                    } else {
                        return newOrSingleton((Class<IExceptionHandler<Throwable>>) eh.value());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .add(getGlobalHandlersStream((Class<Throwable>) throwable.getClass()).toList())
                .forEach(h -> h.handle(throwable));
    }

    @Override
    public <T extends Throwable> void toss(T throwable) {
        try {
            toss(throwable, JStack.getParentMethod());
        } catch (NoSuchMethodException e) {
            runGlobals(throwable);
        }
    }

    @Override
    public <T extends Throwable> void toss(T throwable, Method context) {
        if (context == null) {
            runGlobals(throwable);
            return;
        }

        if (!this.methodHandlers.getOrDefault(context).containsKey(throwable.getClass())
                && !this.globalHandlers.containsKey(throwable.getClass())) {
            if (throwable instanceof RuntimeException e) {
                throw e;
            } else {
                throw new UnhandledException(throwable);
            }
        }

        recover(throwable, context);
    }

    @Override
    public <R> Supplier<R> unboxCallable(Callable<R> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                try {
                    this.toss(e);
                } catch (UnhandledException e2) {
                    throw Exceptions.rethrow(e2);
                }
            }

            return null;
        };
    }

    @Override
    public <R> Supplier<R> unboxCallable(Callable<R> callable, Method context) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                try {
                    this.toss(e, context);
                } catch (UnhandledException e2) {
                    throw Exceptions.rethrow(e2);
                }
            }

            return null;
        };
    }

    @Override
    public void registerGlobal(Class<? extends IExceptionHandler<?>> clazz) {
        if (!clazz.isAnnotationPresent(Handler.class)) {
            throw new RuntimeException("IExceptionHandlers require @Handler metadata!");
        }

        registerGlobal(clazz, clazz.getAnnotation(Handler.class));
    }

    @Override
    public void registerGlobal(Class<? extends IExceptionHandler<?>> clazz, Handler handlerMeta) {
        if (!handlerMeta.global()) {
            logger.debug("Discarding non-global handler: " + clazz);
        }

        logger.info("Registering global handler: " + clazz + " for exception type: " + handlerMeta.value());
        try {
            this.globalHandlers.add(handlerMeta.value(), clazz);
        } catch (Throwable e) {
            throw Exceptions.rethrow(e);
        }
    }

    @Override
    public void registerMethod(Method method, ExceptionHandler handler) {
        if (!handler.value().isAnnotationPresent(Handler.class)) {
            throw new IllegalStateException("Tried to register method exception handler that doesn't have @Handler annotation!");
        }

        Class<? extends Throwable> throwableClazz = handler.value().getAnnotation(Handler.class).value();

        logger.info("Registering method level handler for: " + method + " handling: " + throwableClazz);
        this.methodHandlers.getOrDefault(method).add(throwableClazz, handler);
    }

    @Override
    public <T extends Throwable> List<Class<? extends IExceptionHandler<T>>> getGlobalHandlersFor(Class<T> clazz) {
        return Streams.to(this.globalHandlers.getOrDefault(clazz, new LinkedList<>()))
                .map(c -> (Class<? extends IExceptionHandler<T>>) c)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Class<? extends Throwable>, List<Class<? extends IExceptionHandler<?>>>> getGlobalHandlers() {
        return Collections.unmodifiableMap(this.globalHandlers);
    }

    public <T extends Throwable, H extends IExceptionHandler<T>> Optional<H> newOrSingleton(Class<H> clazz) {
        if (!clazz.isAnnotationPresent(Handler.class)) {
            throw new RuntimeException("IExceptionHandlers require @Handler metadata!");
        }

        Handler handlerMeta = clazz.getAnnotation(Handler.class);

        if (handlerMeta.enforceSingleton()) {
            return getSingleton(clazz);
        }

        try {
            return Optional.of(this.annotationManager.winryConstruct(this.context.get(), clazz));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    public <T extends Throwable, H extends IExceptionHandler<T>> Optional<H> getSingleton(Class<H> clazz) {
        if (this.singletons.containsKey(clazz)) {
            return Optional.ofNullable((H) this.singletons.get(clazz));
        }

        IExceptionHandler<Throwable> handler;
        try {
            handler = (IExceptionHandler<Throwable>) this.annotationManager.winryConstruct(this.context.get(), clazz);
        } catch (Throwable e) {
            return Optional.empty();
        }

        this.singletons.put(clazz, handler);

        return Optional.of((H) handler);
    }

    private <T extends Throwable, H extends IExceptionHandler<T>> Stream<H> getGlobalHandlersStream(Class<T> clazz) {
        return (Stream<H>) Streams.to(getGlobalHandlersFor(clazz))
                .map(this::newOrSingleton)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }

    private void runGlobals(Throwable throwable) {
        List<Class<? extends IExceptionHandler<Throwable>>> globals = getGlobalHandlersFor((Class<Throwable>) throwable.getClass());

        if (globals.isEmpty()) {
            throw Exceptions.rethrow(throwable);
        } else {
            globals.stream()
                    .map(this::newOrSingleton)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(h -> h.handle(throwable));
        }
    }
}
