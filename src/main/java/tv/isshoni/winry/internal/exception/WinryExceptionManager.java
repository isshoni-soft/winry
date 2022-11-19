package tv.isshoni.winry.internal.exception;

import tv.isshoni.araragi.data.collection.map.BucketMap;
import tv.isshoni.araragi.data.collection.map.Maps;
import tv.isshoni.araragi.data.collection.map.SubMap;
import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.api.exception.UnhandledException;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.entity.logging.ILoggerFactory;

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

    public WinryExceptionManager(ILoggerFactory loggerFactory, IWinryAnnotationManager annotationManager) {
        this.logger = loggerFactory.createLogger(getClass());
        this.annotationManager = annotationManager;
        this.globalHandlers = Maps.bucket(new TypeMap<>());
        this.singletons = new TypeMap<>();
        this.methodHandlers = new SubMap<>(() -> Maps.bucket(new TypeMap<>()));
    }

    @Override
    public <T extends Throwable> void toss(T throwable) {
        if (!this.globalHandlers.containsKey(throwable.getClass())) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new UnhandledException(throwable);
            }
        }

        this.logger.debug("Tossing exception: " + throwable);

        getGlobalHandlersFor((Class<T>) throwable.getClass()).stream()
                .map(this::newOrSingleton)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(h -> h.handle(throwable));
    }

    @Override
    public <T extends Throwable> void toss(T throwable, Method context) {
        if (context == null || !this.methodHandlers.containsKey(context)) {
            toss(throwable);
            return;
        }

        if (!this.methodHandlers.getOrDefault(context).containsKey(throwable.getClass()) && !this.globalHandlers.containsKey(throwable.getClass())) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new UnhandledException(throwable);
            }
        }

        this.logger.debug("Tossing exception: " + throwable + " with method context: " + context);

        Streams.to(this.methodHandlers.getOrDefault(context).getOrDefault(throwable.getClass(), new LinkedList<>()))
                .map(eh -> {
                    if (eh.useSingleton()) {
                        return getSingleton((Class<IExceptionHandler<T>>) eh.value());
                    } else {
                        return newOrSingleton((Class<IExceptionHandler<T>>) eh.value());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .add(getGlobalHandlersStream((Class<T>) throwable.getClass()).toList())
                .forEach(h -> h.handle(throwable));
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
        this.methodHandlers.getOrDefault(method).add(throwableClazz, handler);
    }

    @Override
    public <T extends Throwable> List<Class<? extends IExceptionHandler<T>>> getGlobalHandlersFor(Class<T> clazz) {
        return Streams.to(this.globalHandlers.get(clazz))
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
            return Optional.of(this.annotationManager.construct(clazz));
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
            handler = (IExceptionHandler<Throwable>) this.annotationManager.construct(clazz);
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
}
