package tv.isshoni.winry.internal.entity.event;

import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.event.IEvent;
import tv.isshoni.winry.api.bootstrap.WinryEventExecutable;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;

import java.util.List;

public interface IEventBus extends IContextual {

    <T extends IEvent> T fire(T event);

    <T extends IEvent> T fire(Class<T> clazz);

    <T extends IEvent> void registerExecutable(Class<T> clazz);

    <T extends IEvent> void registerExecutable(Class<T> clazz, int weight);

    List<WinryEventExecutable<?>> getExecutableEvents();

    void registerListener(BootstrappedMethod method, Listener listener);

    List<IEventHandler> getHandlersFor(IEvent event);
}
