package tv.isshoni.winry.internal.exception;

import tv.isshoni.araragi.data.collection.map.BucketMap;
import tv.isshoni.araragi.data.collection.map.Maps;
import tv.isshoni.araragi.data.collection.map.SubMap;
import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class WinryExceptionManager implements IExceptionManager {

    private final AraragiLogger logger;

    private final IWinryAnnotationManager annotationManager;

    private final BucketMap<Class<? extends Throwable>, Class<? extends IExceptionHandler<?>>> globalHandlers;
    private final SubMap<Method, Class<? extends Throwable>, List<ExceptionHandler>, BucketMap<Class<? extends Throwable>, ExceptionHandler>> methodHandlers;

    private final TypeMap<Class<? extends IExceptionHandler<?>>, IExceptionHandler<Throwable>> singletons;


    public WinryExceptionManager(LoggerFactory loggerFactory, IWinryAnnotationManager annotationManager) {
        this.logger = loggerFactory.createLogger(getClass());
        this.annotationManager = annotationManager;
        this.globalHandlers = Maps.bucket(new TypeMap<>());
        this.singletons = new TypeMap<>();
        this.methodHandlers = new SubMap<>(() -> Maps.bucket(new TypeMap<>()));
    }

    @Override
    public void toss(Throwable throwable) {
        getGlobalHandlersStream(throwable.getClass())
                .forEach(h -> h.handle(throwable));
    }

    @Override
    public void toss(Throwable throwable, Method context) {
        if (context == null || !this.methodHandlers.containsKey(context)) {
            toss(throwable);
            return;
        }

        Streams.to(this.methodHandlers.getOrDefault(context).getOrDefault(throwable.getClass(), new LinkedList<>()))
                .map(eh -> {
                    if (eh.useSingleton()) {
                        return getSingleton(eh.value());
                    } else {
                        return newOrSingleton(eh.value());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .add(getGlobalHandlersStream(throwable.getClass()).toList())
                .forEach(h -> h.handle(throwable));
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
            throw new RuntimeException(e);
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
    public List<Class<? extends IExceptionHandler<?>>> getGlobalHandlersFor(Class<? extends Throwable> clazz) {
        return this.globalHandlers.get(clazz);
    }

    @Override
    public Map<Class<? extends Throwable>, List<Class<? extends IExceptionHandler<?>>>> getGlobalHandlers() {
        return Collections.unmodifiableMap(this.globalHandlers);
    }

    public Optional<IExceptionHandler<Throwable>> newOrSingleton(Class<? extends IExceptionHandler<?>> clazz) {
        if (!clazz.isAnnotationPresent(Handler.class)) {
            throw new RuntimeException("IExceptionHandlers require @Handler metadata!");
        }

        Handler handlerMeta = clazz.getAnnotation(Handler.class);

        if (handlerMeta.enforceSingleton()) {
            return getSingleton(clazz);
        }

        try {
            return Optional.of((IExceptionHandler<Throwable>) this.annotationManager.construct(clazz));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    public Optional<IExceptionHandler<Throwable>> getSingleton(Class<? extends IExceptionHandler<?>> clazz) {
        if (this.singletons.containsKey(clazz)) {
            return Optional.ofNullable(this.singletons.get(clazz));
        }

        IExceptionHandler<Throwable> handler;
        try {
            handler = (IExceptionHandler<Throwable>) this.annotationManager.construct(clazz);
        } catch (Throwable e) {
            return Optional.empty();
        }

        this.singletons.put(clazz, handler);

        return Optional.of(handler);
    }

    private Stream<IExceptionHandler<Throwable>> getGlobalHandlersStream(Class<? extends Throwable> clazz) {
        return Streams.to(getGlobalHandlersFor(clazz))
                .map(this::newOrSingleton)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
