package tv.isshoni.winry.internal.event;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.ExecutableEvent;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.event.ICancellable;
import tv.isshoni.winry.api.event.IEvent;
import tv.isshoni.winry.api.bootstrap.WinryEventExecutable;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.event.IEventBus;
import tv.isshoni.winry.internal.entity.event.IEventHandler;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.entity.logging.ILoggerFactory;

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
    private final IExceptionManager exceptionManager;

    private final AraragiLogger LOGGER;

    public WinryEventBus(IWinryAsyncManager asyncManager, ILoggerFactory loggerFactory,
                         IWinryAnnotationManager annotationManager, IExceptionManager exceptionManager) {
        this.asyncManager = asyncManager;
        this.annotationManager = annotationManager;
        this.exceptionManager = exceptionManager;
        this.LOGGER = loggerFactory.createLogger("EventBus");
        this.executableEvents = new LinkedList<>();
        this.handlers = new HashMap<>();
    }

    @Override
    public <T extends IEvent> T fire(T event) {
        this.LOGGER.debug("Firing event: " + event.getName());
        List<IEventHandler> handlers = getHandlersFor(event);

        Consumer<? super IEventHandler> execute = h -> {
            try {
                h.execute(event);
            } catch (Throwable throwable) {
                try {
                    this.exceptionManager.toss(throwable, h.getClass().getMethod("execute", IEvent.class));
                } catch (NoSuchMethodException e) {
                    throw new EventExecutionException(event.getClass(), e);
                }
            }
        };

        Consumer<? super IEventHandler> runner = h -> {
            if (event instanceof ICancellable) {
                if (((ICancellable) event).isCancelled() && !h.shouldIgnoreCancelled()) {
                    return;
                }
            }

            if (h.needsMainThread()) {
                this.asyncManager.submitToMain(() -> execute.accept(h));
            } else {
                execute.accept(h);
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
            this.exceptionManager.toss(throwable);
            throw new EventExecutionException(clazz, throwable);
        }

        try {
            return fire(event);
        } catch (EventExecutionException e) {
            throw e;
        } catch (Throwable throwable) {
            this.exceptionManager.toss(throwable);
            throw new EventExecutionException(clazz, throwable);
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
