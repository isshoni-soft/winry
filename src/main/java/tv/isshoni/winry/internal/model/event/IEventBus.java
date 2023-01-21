package tv.isshoni.winry.internal.model.event;

import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

import java.util.List;
import java.util.function.Consumer;

public interface IEventBus extends IContextual {

    Event findAnnotation(Object o);

    boolean isEvent(Object o);

    <T> T fire(T event) throws EventExecutionException;

    <T> T fire(T event, boolean block) throws EventExecutionException;

    <T> T fire(Class<T> clazz) throws EventExecutionException;

    <T> T fire(Class<T> clazz, boolean block) throws EventExecutionException;

    void registerExecutable(Class<?> clazz);

    void registerExecutable(Class<?> clazz, int weight);

    <T> void registerListener(Consumer<T> handler, Class<T> type, int weight);

    void registerListener(IAnnotatedMethod method, Object target, Listener listener);

    List<IEventHandler<Object>> getHandlersFor(Object event);
}
