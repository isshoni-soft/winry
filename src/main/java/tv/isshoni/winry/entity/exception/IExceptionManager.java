package tv.isshoni.winry.entity.exception;

import java.util.List;

public interface IExceptionManager {

    void toss(Throwable throwable);

    void register(Class<? extends IExceptionHandler<?>> handler);

    <E extends Throwable> List<IExceptionHandler<E>> getHandlersFor(Class<E> clazz);
}

// Exception occurs -> A/B Choice
// A: Method does not have special ExceptionHandler annotation
// B:
