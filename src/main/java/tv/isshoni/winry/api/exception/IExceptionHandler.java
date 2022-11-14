package tv.isshoni.winry.api.exception;

public interface IExceptionHandler<E extends Throwable> {

    void handle(E exception);
}
