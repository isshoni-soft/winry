package tv.isshoni.winry.internal.event;

import tv.isshoni.araragi.data.collection.map.BucketMap;
import tv.isshoni.araragi.data.collection.map.Maps;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.bootstrap.WinryEventExecutable;
import tv.isshoni.winry.api.event.ICancellable;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.event.IEventBus;
import tv.isshoni.winry.internal.entity.event.IEventHandler;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.entity.logging.ILoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

public class WinryEventBus implements IEventBus {

    private final BucketMap<Class<?>, IEventHandler> handlers;

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
        this.handlers = Maps.bucket(new HashMap<>());
    }

    @Override
    public Event findAnnotation(Object o) {
        Class<?> clazz;
        if (o instanceof Class<?>) {
            clazz = (Class<?>) o;
        } else {
            clazz = o.getClass();
        }

        return clazz.getAnnotation(Event.class);
    }

    @Override
    public boolean isEvent(Object o) {
        return findAnnotation(o) != null;
    }

    @Override
    public <T> T fire(T event) {
        Event eventMeta = findAnnotation(event);

        if (eventMeta == null) {
            throw new IllegalArgumentException("Event type missing Event annotation!");
        }

        if (eventMeta.value().isBlank()) {
            throw new IllegalArgumentException("Event must have a name!");
        }

        this.LOGGER.debug("Firing event: " + eventMeta.value());
        List<IEventHandler> handlers = getHandlersFor(event);

        Consumer<? super IEventHandler> execute = h -> {
            try {
                h.execute(event);
            } catch (Throwable throwable) {
                try {
                    this.exceptionManager.toss(throwable, h.getClass().getMethod("execute", Object.class));
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

        if (eventMeta.async()) {
            consumer = h -> this.asyncManager.submit(() -> runner.accept(h));
        }

        handlers.forEach(consumer);

        return event;
    }

    @Override
    public <T> T fire(Class<T> clazz) {
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
    public void registerExecutable(Class<?> clazz) {
        Event event = findAnnotation(clazz);

        if (event == null) {
            return;
        }

        if (!event.executable()) {
            return;
        }

        registerExecutable(clazz, event.weight());
    }

    @Override
    public void registerExecutable(Class<?> clazz, int weight) {
        getWinryContext().registerExecutable(new WinryEventExecutable(clazz, weight, this));
    }

    @Override
    public void registerListener(BootstrappedMethod method, Listener listener) {
        this.handlers.add(listener.value(), new WinryEventHandler(method, listener));
    }

    @Override
    public List<IEventHandler> getHandlersFor(Object event) {
        Class<?> current = event.getClass();
        List<IEventHandler> result = new LinkedList<>();
        Stack<Class<?>> next = new Stack<>();
        Consumer<Class<?>> consumer = c -> {
            if (Objects.nonNull(c) && isEvent(c)) {
                next.push(c);
            }
        };

        while (Objects.nonNull(current) && isEvent(current)) {
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
