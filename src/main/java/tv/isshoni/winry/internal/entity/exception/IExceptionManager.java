package tv.isshoni.winry.internal.entity.exception;

import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface IExceptionManager {

    void toss(Throwable throwable);

    void toss(Throwable throwable, Method context);

    void registerGlobal(Class<? extends IExceptionHandler<?>> clazz);

    void registerGlobal(Class<? extends IExceptionHandler<?>> clazz, Handler handlerMeta);

    void registerMethod(Method method, ExceptionHandler handler);

    List<Class<? extends IExceptionHandler<?>>> getGlobalHandlersFor(Class<? extends Throwable> clazz);

    Map<Class<? extends Throwable>, List<Class<? extends IExceptionHandler<?>>>> getGlobalHandlers();
}
