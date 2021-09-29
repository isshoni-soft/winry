package tv.isshoni.winry.entity.event;

import tv.isshoni.winry.api.entity.context.IContextual;
import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.api.entity.event.IEventHandler;

import java.lang.reflect.Method;
import java.util.List;

public interface IEventBus extends IContextual {

    <T extends IEvent> T fire(T event);

    void fire(Class<? extends IEvent> clazz);

    void registerListener(Method method);

    List<IEventHandler> getHandlersFor(IEvent event);
}
