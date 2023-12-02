package tv.isshoni.winry.api.context;

import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.event.IEventHandler;

import java.util.List;
import java.util.function.Consumer;

public interface IEventBus {

    Event findAnnotation(Object o);

    boolean isEvent(Object o);

    <T> T fire(T event) throws EventExecutionException;

    <T> T fire(Class<T> clazz) throws EventExecutionException;

    void fireAsync(Object event) throws EventExecutionException;

    void fireAsync(Object event, boolean block) throws EventExecutionException;

    void fireAsync(Class<?> event) throws EventExecutionException;

    void fireAsync(Class<?> event, boolean block) throws EventExecutionException;

    void provideExecutable(IWinryContext context, Class<?> clazz);

    void provideExecutable(IWinryContext context, Class<?> clazz, int weight);

    <T> void registerListener(Consumer<T> handler, Class<T> type, int weight);

    void registerListener(IAnnotatedMethod method, Object target, Listener listener);

    void registerListeners(Object target);

    void unregisterListeners(Object target);

    void unregisterListeners(Object target, Class<?> event);

    List<IEventHandler<Object>> getHandlersFor(Object event);
}
