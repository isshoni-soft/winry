package tv.isshoni.winry.internal.exception;

import tv.isshoni.araragi.data.collection.BucketMap;
import tv.isshoni.araragi.data.collection.Maps;
import tv.isshoni.araragi.data.collection.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WinryExceptionManager implements IExceptionManager {

    private final AraragiLogger logger;

    private final IWinryAnnotationManager annotationManager;

    private final BucketMap<Class<? extends Throwable>, IExceptionHandler<?>> globalHandlers;

    private final TypeMap<Class<? extends IExceptionHandler<?>>, IExceptionHandler<?>> singletons;

    public WinryExceptionManager(LoggerFactory loggerFactory, IWinryAnnotationManager annotationManager) {
        this.logger = loggerFactory.createLogger(getClass());
        this.annotationManager = annotationManager;
        this.globalHandlers = Maps.bucket(new TypeMap<>());
        this.singletons = new TypeMap<>();
    }

    @Override
    public void toss(Throwable throwable) {

    }

    @Override
    public void toss(Throwable throwable, Method context) {
        if (context == null) {
            toss(throwable);
            return;
        }


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
            IExceptionHandler<?> handler = this.annotationManager.construct(clazz);

            this.globalHandlers.add(handlerMeta.value(), handler);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E extends Throwable> List<IExceptionHandler<E>> getHandlersFor(Class<E> clazz) {
        return null;
    }

    public Map<Class<? extends Throwable>, List<IExceptionHandler<?>>> getGlobalHandlers() {
        return Collections.unmodifiableMap(this.globalHandlers);
    }
}
