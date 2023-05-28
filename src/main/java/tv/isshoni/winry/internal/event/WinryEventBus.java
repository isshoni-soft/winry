package tv.isshoni.winry.internal.event;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.data.collection.map.BucketMap;
import tv.isshoni.araragi.data.collection.map.Maps;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.bootstrap.executable.EventExecutable;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.context.IExceptionManager;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.ICancellable;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.event.IEventHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WinryEventBus implements IEventBus {

    private final BucketMap<Class<?>, IEventHandler<?>> handlers;

    private final IWinryAnnotationManager annotationManager;

    private final AraragiLogger logger;

    public WinryEventBus(IWinryAsyncManager asyncManager, ILoggerFactory loggerFactory,
                         IWinryAnnotationManager annotationManager, IExceptionManager exceptionManager) {
        this.annotationManager = annotationManager;
        this.logger = loggerFactory.createLogger("EventBus");
        this.handlers = Maps.bucket(new HashMap<>());

        registerListener(event -> asyncManager.shutdown(), WinryShutdownEvent.class, Integer.MIN_VALUE);
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
    public <T> T fire(T event) throws EventExecutionException {
        return this.fire(event, true);
    }

    @Override
    public <T> T fire(T event, boolean block) throws EventExecutionException {
        Event eventMeta = findAnnotation(event);

        if (eventMeta == null) {
            throw new IllegalArgumentException("Event type missing Event annotation!");
        }

        if (eventMeta.value().isBlank()) {
            throw new IllegalArgumentException("Event must have a name!");
        }

        this.logger.debug("Firing event: " + eventMeta.value());
        List<IEventHandler<Object>> handlers = getHandlersFor(event);
        Set<IEventHandler<Object>> await = new HashSet<>();

        for (IEventHandler<Object> h : handlers) {
            if (event instanceof ICancellable) {
                if (((ICancellable) event).isCancelled() && !h.shouldIgnoreCancelled()) {
                    continue;
                }
            }

            try {
                synchronized (await) {
                    await.add(h);
                }

                h.execute(event);
            } catch (Throwable throwable) {
                throw new EventExecutionException(event.getClass(), throwable);
            } finally {
                synchronized (await) {
                    await.remove(h);

                    if (await.isEmpty()) {
                        await.notify();
                    }
                }
            }
        }

        if (block && !await.isEmpty()) {
            synchronized (await) {
                try {
                    await.wait();
                } catch (InterruptedException e) {
                    throw new EventExecutionException(event.getClass(), e);
                }
            }
        }

        return event;
    }

    @Override
    public <T> T fire(Class<T> clazz) throws EventExecutionException {
        return this.fire(clazz, true);
    }

    @Override
    public <T> T fire(Class<T> clazz, boolean block) throws EventExecutionException {
        T event;
        try {
            event = this.annotationManager.construct(clazz);
        } catch (Throwable throwable) {
            throw new EventExecutionException(clazz, throwable);
        }

        try {
            return fire(event, block);
        } catch (EventExecutionException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new EventExecutionException(clazz, throwable);
        }
    }

    @Override
    public void provideExecutable(IWinryContext context, Class<?> clazz) {
        Event event = findAnnotation(clazz);
        Map<String, Supplier<String>> logParams = Maps.ofPairs(Pair.of("name", clazz::getSimpleName));

        if (event == null) {
            this.logger.error("Cannot find @Event for: ${name}", logParams);
            return;
        }

        if (!event.executable()) {
            this.logger.error("@Event: ${name} is not executable!", logParams);
            return;
        }

        this.logger.debug("Providing executable event: ${name}", logParams);
        provideExecutable(context, clazz, event.weight());
    }

    @Override
    public void provideExecutable(IWinryContext context, Class<?> clazz, int weight) {
        context.registerExecutable(new EventExecutable(clazz, weight, context));
    }

    @Override
    public <T> void registerListener(Consumer<T> handler, Class<T> type, int weight) {
        this.handlers.add(type, new WinryLambdaEventHandler<>(handler, type, weight));
    }

    @Override
    public void registerListener(IAnnotatedMethod method, Object target, Listener listener) {
        this.handlers.add(listener.value(), new EventHandlerMeta(method, target, listener));
    }

    @Override
    public List<IEventHandler<Object>> getHandlersFor(Object event) {
        Class<?> current = event.getClass();
        List<IEventHandler<Object>> result = new LinkedList<>();
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
                result.addAll(Streams.to(this.handlers.get(current)).map(h -> (IEventHandler<Object>) h).toList());
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
