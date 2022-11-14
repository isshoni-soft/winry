package tv.isshoni.winry.api.exception;

import tv.isshoni.winry.api.event.IEvent;

public class EventExecutionException extends RuntimeException {

    public EventExecutionException(Class<? extends IEvent> clazz, Throwable throwable) {
        super("Failed to execute event: " + clazz.getName(), throwable);
    }
}
