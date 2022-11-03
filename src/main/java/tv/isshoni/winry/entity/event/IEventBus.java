package tv.isshoni.winry.entity.event;

import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.entity.context.IContextual;
import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.api.entity.event.WinryEventExecutable;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

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
