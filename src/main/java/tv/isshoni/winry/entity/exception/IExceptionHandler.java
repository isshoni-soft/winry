package tv.isshoni.winry.entity.exception;

public interface IExceptionHandler<E extends Throwable> {

    void handle(E exception);
}
