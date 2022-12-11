package tv.isshoni.winry.api.exception;

public class EventExecutionException extends RuntimeException {

    public EventExecutionException(Class<?> clazz, Throwable throwable) {
        super("Failed to execute event: " + clazz.getName(), throwable);
    }
}
