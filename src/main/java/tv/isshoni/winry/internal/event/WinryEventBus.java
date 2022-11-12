package tv.isshoni.winry.internal.event;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.ExecutableEvent;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.entity.event.ICancellable;
import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.api.bootstrap.WinryEventExecutable;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.async.IWinryAsyncManager;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.event.IEventBus;
import tv.isshoni.winry.entity.event.IEventHandler;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

public class WinryEventBus implements IEventBus {

    private final Map<Class<? extends IEvent>, List<IEventHandler>> handlers;

    private final List<WinryEventExecutable<?>> executableEvents;

    private final IWinryAsyncManager asyncManager;
    private final IWinryAnnotationManager annotationManager;

    private final AraragiLogger LOGGER;

    public WinryEventBus(IWinryAsyncManager asyncManager, ILoggerFactory loggerFactory, IWinryAnnotationManager annotationManager) {
        this.asyncManager = asyncManager;
        this.annotationManager = annotationManager;
        this.LOGGER = loggerFactory.createLogger("EventBus");
        this.executableEvents = new LinkedList<>();
        this.handlers = new HashMap<>();
    }

    @Override
    public <T extends IEvent> T fire(T event) {
        this.LOGGER.debug("Firing event: " + event.getName());
        List<IEventHandler> handlers = getHandlersFor(event);

        Consumer<? super IEventHandler> runner = h -> {
            if (event instanceof ICancellable) {
                if (((ICancellable) event).isCancelled() && !h.shouldIgnoreCancelled()) {
                    return;
                }
            }

            if (h.needsMainThread()) {
                // TODO: Submit execute to main thread.
            } else {
                h.execute(event);
            }
        };

        Consumer<? super IEventHandler> consumer = runner;

        if (event.isAsync()) {
            consumer = h -> this.asyncManager.submit(() -> runner.accept(h));
        }

        handlers.forEach(consumer);

        return event;
    }

    @Override
    public <T extends IEvent> T fire(Class<T> clazz) {
        T event;
        try {
            event = this.annotationManager.construct(clazz);
        } catch (Throwable throwable) {
            throw new RuntimeException("Encountered exception during event construction", throwable);
        }

        try {
            return fire(event);
        } catch (Throwable throwable) {
            throw new RuntimeException("Encountered exception while firing event: " + event.getName(), throwable);
        }
    }

    @Override
    public <T extends IEvent> void registerExecutable(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(ExecutableEvent.class)) {
            return;
        }

        registerExecutable(clazz, clazz.getAnnotation(ExecutableEvent.class).value());
    }

    @Override
    public <T extends IEvent> void registerExecutable(Class<T> clazz, int weight) {
        this.executableEvents.add(new WinryEventExecutable<>(clazz, weight, this));
    }

    @Override
    public List<WinryEventExecutable<?>> getExecutableEvents() {
        return Collections.unmodifiableList(this.executableEvents);
    }

    @Override
    public void registerListener(BootstrappedMethod method, Listener listener) {
        if (!this.handlers.containsKey(listener.value())) {
            this.handlers.put(listener.value(), new LinkedList<>());
        }

        List<IEventHandler> handlers = this.handlers.get(listener.value());
        handlers.add(new WinryEventHandler(method, listener));
    }

    @Override
    public List<IEventHandler> getHandlersFor(IEvent event) {
        Class<?> current = event.getClass();
        List<IEventHandler> result = new LinkedList<>();
        Stack<Class<?>> next = new Stack<>();
        Consumer<Class<?>> consumer = c -> {
            if (Objects.nonNull(c) && IEvent.class.isAssignableFrom(c)) {
                next.push(c);
            }
        };

        while (Objects.nonNull(current) && IEvent.class.isAssignableFrom(current)) {
            consumer.accept(current.getSuperclass());
            Streams.to(current.getInterfaces()).forEach(consumer);

            if (this.handlers.containsKey(current)) {
                result.addAll(this.handlers.get(current));
            }

            if (next.isEmpty()) {
                current = null;
            } else {
                current = next.pop();
            }
        }

        Collections.sort(result);

        return result;
    }
}
