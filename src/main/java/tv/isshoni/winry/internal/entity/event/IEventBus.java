package tv.isshoni.winry.internal.entity.event;

import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;

import java.util.List;

public interface IEventBus extends IContextual {

    Event findAnnotation(Object o);

    boolean isEvent(Object o);

    <T> T fire(T event);

    <T> T fire(Class<T> clazz);

    void registerExecutable(Class<?> clazz);

    void registerExecutable(Class<?> clazz, int weight);

    void registerListener(BootstrappedMethod method, Listener listener);

    List<IEventHandler> getHandlersFor(Object event);
}
