package institute.isshoni.winry.api.context;

import institute.isshoni.winry.api.annotation.Event;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.exception.EventExecutionException;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.internal.model.event.IEventHandler;

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

    void registerListeners(Object target, Class<?> event);

    void unregisterListeners(Object target);

    void unregisterListeners(Object target, Class<?> event);

    List<IEventHandler<Object>> getHandlersFor(Object event);

    void blockRegistration(Class<?> clazz);

    void unblockRegistration(Class<?> clazz);

    boolean isRegistrationBlocked(Class<?> clazz);
}
