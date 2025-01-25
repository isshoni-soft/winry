package institute.isshoni.winry.internal.event;

import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.data.collection.map.BucketMap;
import institute.isshoni.araragi.data.collection.map.Maps;
import institute.isshoni.araragi.data.collection.map.TypeMap;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.winry.api.WReflect;
import institute.isshoni.winry.api.annotation.Event;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.async.IWinryAsyncManager;
import institute.isshoni.winry.api.bootstrap.executable.EventExecutable;
import institute.isshoni.winry.api.context.IEventBus;
import institute.isshoni.winry.api.context.IExceptionManager;
import institute.isshoni.winry.api.context.IInstanceManager;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.event.ICancellable;
import institute.isshoni.winry.api.event.WinryShutdownEvent;
import institute.isshoni.winry.api.exception.EventExecutionException;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.api.meta.IMetaManager;
import institute.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import institute.isshoni.winry.internal.model.event.IEventHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WinryEventBus implements IEventBus {

    private final BucketMap<Class<?>, IEventHandler<?>> handlers;

    private final Set<Class<?>> blockRegistration;

    private final IWinryAnnotationManager annotationManager;

    private final IWinryAsyncManager asyncManager;

    private final IExceptionManager exceptionManager;

    private final IInstanceManager instanceManager;

    private final IMetaManager metaManager;

    private final AraragiLogger logger;

    public WinryEventBus(IWinryAsyncManager asyncManager, ILoggerFactory loggerFactory, IMetaManager metaManager,
                         IWinryAnnotationManager annotationManager, IExceptionManager exceptionManager,
                         IInstanceManager instanceManager) {
        this.annotationManager = annotationManager;
        this.asyncManager = asyncManager;
        this.metaManager = metaManager;
        this.exceptionManager = exceptionManager;
        this.instanceManager = instanceManager;
        this.logger = loggerFactory.createLogger("EventBus");
        this.handlers = Maps.bucket(new TypeMap<>());
        this.blockRegistration = new HashSet<>();

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
        Event eventMeta = findAnnotation(event);

        if (eventMeta == null) {
            throw new IllegalArgumentException("Event type missing Event annotation!");
        }

        if (eventMeta.value().isBlank()) {
            throw new IllegalArgumentException("Event must have a name!");
        }

        this.logger.debug("Firing event: " + eventMeta.value());
        List<IEventHandler<Object>> handlers = getHandlersFor(event);

        for (IEventHandler<Object> h : handlers) {
            if (this.cancelEvent(event, h)) {
                continue;
            }

            try {
                h.execute(event);
            }  catch (Throwable throwable) {
                this.exceptionManager.toss(throwable);
                return null;
            }
        }

        return event;
    }

    @Override
    public <T> T fire(Class<T> clazz) throws EventExecutionException {
        T event;
        try {
            event = this.annotationManager.construct(clazz);
        } catch (Throwable throwable) {
            throw new EventExecutionException(clazz, throwable);
        }

        try {
            return fire(event);
        } catch (EventExecutionException e) {
            throw e;
        } catch (Throwable throwable) {
            this.exceptionManager.toss(throwable);
            return null;
        }
    }

    @Override
    public void fireAsync(Object event) throws EventExecutionException {
        fireAsync(event, false);
    }

    @Override
    public void fireAsync(Object event, boolean block) throws EventExecutionException {
        Event eventMeta = findAnnotation(event);

        if (eventMeta == null) {
            throw new IllegalArgumentException("Event type missing Event annotation!");
        }

        if (eventMeta.value().isBlank()) {
            throw new IllegalArgumentException("Event must have a name!");
        }

        this.logger.debug("Firing async event: " + eventMeta.value());
        List<IEventHandler<Object>> handlers = getHandlersFor(event);
        Set<IEventHandler<Object>> await = new HashSet<>();

        for (IEventHandler<Object> h : handlers) {
            if (this.cancelEvent(event, h)) {
                continue;
            }

            this.asyncManager.submit(() -> {
                try {
                    synchronized (await) {
                        await.add(h);
                    }

                    h.execute(event);
                } catch (Throwable throwable) {
                    this.exceptionManager.toss(throwable);
                } finally {
                    synchronized (await) {
                        await.remove(h);

                        if (await.isEmpty()) {
                            await.notify();
                        }
                    }
                }
            });
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
    }

    @Override
    public void fireAsync(Class<?> event) throws EventExecutionException {
        fireAsync(event, false);
    }

    @Override
    public void fireAsync(Class<?> clazz, boolean block) throws EventExecutionException {
        Object event;
        try {
            event = this.annotationManager.construct(clazz);
        } catch (Throwable throwable) {
            this.exceptionManager.toss(throwable);
            return;
        }

        try {
            fireAsync(event);
        } catch (EventExecutionException e) {
            throw e;
        } catch (Throwable throwable) {
            this.exceptionManager.toss(throwable);
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
        if (this.isRegistrationBlocked(type)) {
            return;
        }

        this.handlers.add(type, new WinryLambdaEventHandler<>(handler, type, weight));
    }

    @Override
    public void registerListener(IAnnotatedMethod method, Object target, Listener listener) {
        if (this.isRegistrationBlocked(target.getClass())) {
            return;
        }

        this.handlers.add(listener.value(), new EventHandlerMeta(method, target, listener));
    }

    @Override
    public void registerListeners(Object target) {
        this.registerListeners(target, Object.class);
    }

    @Override
    public void registerListeners(Object target, Class<?> event) {
        Optional<Object> singleton = this.instanceManager.hasSingletonFor(target.getClass());

        IAnnotatedClass annotatedClass;
        if (singleton.isPresent()) {
            annotatedClass = this.metaManager.getSingletonMeta(singleton.get());
        } else {
            annotatedClass = this.metaManager.getMeta(target);
        }

        annotatedClass.getMethods().forEach(method -> {
            if (method.hasAnnotations(Listener.class)) {
                Listener listener = method.getAnnotationByType(Listener.class);

                if (!event.isAssignableFrom(listener.value())) {
                    return;
                }

                this.registerListener(method, target, listener);
            }
        });
    }

    @Override
    public void unregisterListeners(Object target) {
        this.unregisterListeners(target, Object.class);
    }

    @Override
    public void unregisterListeners(Object target, Class<?> event) {
        this.handlers.forEach((k, v) -> {
            Set<EventHandlerMeta> removeHandlers = new HashSet<>();

            v.forEach(handler -> {
                if (handler instanceof EventHandlerMeta eventMeta) {
                    if (eventMeta.getTarget().equals(target) && event.isAssignableFrom(handler.getTargetEvent())) {
                        removeHandlers.add(eventMeta);
                    }
                }
            });

            v.removeAll(removeHandlers);
        });
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

    @Override
    public void blockRegistration(Class<?> clazz) {
        this.blockRegistration.add(WReflect.getClass(clazz));
    }

    @Override
    public void unblockRegistration(Class<?> clazz) {
        this.blockRegistration.remove(WReflect.getClass(clazz));
    }

    @Override
    public boolean isRegistrationBlocked(Class<?> clazz) {
        return this.blockRegistration.contains(WReflect.getClass(clazz));
    }

    private boolean cancelEvent(Object event, IEventHandler<?> h) {
        if (event instanceof ICancellable) {
            if (((ICancellable) event).isCancelled() && !h.shouldIgnoreCancelled()) {
                return true;
            }
        }

        if (h.requiresExact() && !event.getClass().equals(h.getTargetEvent())) {
            return true;
        }

        return false;
    }

    private TypeMap<Class<?>, List<IEventHandler<?>>> getUnwrappedHandlers() {
        return (TypeMap<Class<?>, List<IEventHandler<?>>>) this.handlers.getWrapped();
    }
}
