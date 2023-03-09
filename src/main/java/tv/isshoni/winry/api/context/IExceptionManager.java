package tv.isshoni.winry.api.context;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IExceptionManager {

    Constant<IWinryContext> getContext();

    void recover(Throwable throwable);

    void recover(Throwable throwable, Method context);

    <T extends Throwable> void toss(T throwable);

    <T extends Throwable> void toss(T throwable, Method context);

    <R> Supplier<R> unboxCallable(Callable<R> callable);

    <R> Supplier<R> unboxCallable(Callable<R> callable, Method context);

    void registerGlobal(Class<? extends IExceptionHandler<?>> clazz);

    void registerGlobal(Class<? extends IExceptionHandler<?>> clazz, Handler handlerMeta);

    void registerMethod(Method method, ExceptionHandler handler);

    <T extends Throwable> List<Class<? extends IExceptionHandler<T>>> getGlobalHandlersFor(Class<T> clazz);

    Map<Class<? extends Throwable>, List<Class<? extends IExceptionHandler<?>>>> getGlobalHandlers();

    <T extends Throwable, H extends IExceptionHandler<T>> Optional<H> getSingleton(Class<H> clazz);
}
