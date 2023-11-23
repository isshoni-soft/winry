package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.context.IWinryContext;

public interface IListener extends AutoCloseable, IContextual {

    default void close() {
        IWinryContext context = getContext().get();

        IEventBus eventBus = context.getEventBus();
        eventBus.unregisterListeners(this);
    }
}
