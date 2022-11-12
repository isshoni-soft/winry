package tv.isshoni.winry.internal.exception;

import tv.isshoni.araragi.data.collection.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class WinryExceptionManager implements IExceptionManager {

    private final AraragiLogger logger;

    private final TypeMap<Class<? extends Exception>, List<IExceptionHandler<?>>> globalHandlers;

    public WinryExceptionManager(LoggerFactory loggerFactory) {
        this.logger = loggerFactory.createLogger(getClass());
        this.globalHandlers = new TypeMap<>();
    }

    @Override
    public void toss(Throwable throwable) {

    }

    @Override
    public void toss(Throwable throwable, Method context) {

    }

    @Override
    public void register(Class<? extends IExceptionHandler<?>> handler) {

    }

    @Override
    public <E extends Throwable> List<IExceptionHandler<E>> getHandlersFor(Class<E> clazz) {
        return null;
    }
}
