package institute.isshoni.winry.api.exception;

public class EventExecutionException extends Exception {

    public EventExecutionException(Class<?> clazz, Throwable throwable) {
        super("Failed to execute event: " + clazz.getName(), throwable);
    }
}
