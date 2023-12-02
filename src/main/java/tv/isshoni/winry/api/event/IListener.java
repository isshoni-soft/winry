package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.IEventBus;

import java.io.Closeable;

public interface IListener extends AutoCloseable, IContextual, Closeable {

    default IEventBus getEventBus() {
        return getContext().get().getEventBus();
    }

    default void reregister() {
        getEventBus().registerListeners(this);
    }

    default void unregister(Class<?> event) {
        getEventBus().unregisterListeners(this, event);
    }

    default void close() {
        getEventBus().unregisterListeners(this);
    }
}
