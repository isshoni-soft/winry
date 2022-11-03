package tv.isshoni.winry.api.entity.context;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.inject.IInjectionRegistry;
import tv.isshoni.winry.entity.async.IWinryAsyncManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.event.IEventBus;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WinryContext implements IWinryContext {

    private static final AtomicInteger CONTEXT_ID = new AtomicInteger(0);

    private static final Map<Object, IWinryContext> CONTEXT_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Integer, IWinryContext> CONTEXT_BY_ID = new ConcurrentHashMap<>();

    public static Optional<IWinryContext> getContextFor(Object object) {
        return Optional.ofNullable(CONTEXT_REGISTRY.get(object));
    }

    public static Optional<IWinryContext> getContextById(int id) {
        return Optional.ofNullable(CONTEXT_BY_ID.get(id));
    }

    public static Collection<IWinryContext> getContexts() {
        return CONTEXT_REGISTRY.values();
    }

    public static Map<Object, IWinryContext> getContextRegistry() {
        return Collections.unmodifiableMap(CONTEXT_REGISTRY);
    }

    private final int id;

    private final Instant created;

    private final AraragiLogger logger;

    private final IBootstrapper bootstrapper;

    private final IWinryAnnotationManager annotationManager;

    private final ILoggerFactory loggerFactory;

    private final IElementBootstrapper elementBootstrapper;

    private final IWinryAsyncManager asyncManager;

    private final IEventBus eventBus;

    private final IInjectionRegistry injectionRegistry;

    private final List<IExecutable> executables;

    private final Bootstrap bootstrap;

    public WinryContext(Builder builder) {
        this.id = CONTEXT_ID.getAndIncrement();
        this.created = Instant.now();
        this.bootstrap = builder.bootstrap;
        this.bootstrapper = builder.bootstrapper;
        this.annotationManager = builder.annotationManager;
        this.loggerFactory = builder.loggerFactory;
        this.asyncManager = builder.asyncManager;
        this.elementBootstrapper = builder.elementBootstrapper;
        this.eventBus = builder.eventBus;
        this.injectionRegistry = builder.injectionRegistry;
        this.executables = new LinkedList<>();
        this.logger = this.loggerFactory.createLogger("WinryContext [" + this.id + "]");

        registerToContext(this.bootstrapper);
        registerToContext(this.annotationManager);
        registerToContext(this.loggerFactory);
        registerToContext(this.elementBootstrapper);
        registerToContext(this.bootstrap);
        registerToContext(this.asyncManager);
        registerToContext(this.eventBus);
        registerToContext(this.injectionRegistry);

        CONTEXT_BY_ID.put(this.id, this);
    }

    @Override
    public int getContextId() {
        return this.id;
    }

    @Override
    public void registerToContext(Object object) {
        CONTEXT_REGISTRY.putIfAbsent(object, this);

        this.logger.debug("Context: Registering: " + object);
    }

    @Override
    public void registerToContext(Object... objects) {
        Streams.to(objects).forEach(this::registerToContext);
    }

    @Override
    public void registerExecutable(IExecutable executable) {
        this.executables.add(executable);

        this.logger.debug("Executable: Registering: " + executable.getDisplay());
    }

    @Override
    public void registerExecutable(IExecutable... executable) {
        Streams.to(executable).forEach(this::registerExecutable);
    }

    @Override
    public Instant getCreated() {
        return this.created;
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    @Override
    public IElementBootstrapper getElementBootstrapper() {
        return this.elementBootstrapper;
    }

    @Override
    public IWinryAsyncManager getAsyncManager() {
        return this.asyncManager;
    }

    @Override
    public IEventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public IInjectionRegistry getInjectionRegistry() {
        return this.injectionRegistry;
    }

    @Override
    public List<IExecutable> getExecutables() {
        return Collections.unmodifiableList(this.executables);
    }

    @Override
    public Bootstrap getBootstrapAnnotation() {
        return this.bootstrap;
    }

    @Override
    public String toString() {
        return "WinryContext[bootstrapper=" + this.bootstrapper.getClass().getName() + ",created=" + this.created.toString() + "]";
    }

    public static Builder builder(Bootstrap bootstrap, IBootstrapper bootstrapper) {
        Builder builder = new Builder();
        builder.bootstrap = bootstrap;
        builder.bootstrapper = bootstrapper;

        return builder;
    }

    public static class Builder {
        private Bootstrap bootstrap;
        private IBootstrapper bootstrapper;
        private IWinryAnnotationManager annotationManager;
        private ILoggerFactory loggerFactory;
        private IElementBootstrapper elementBootstrapper;
        private IWinryAsyncManager asyncManager;
        private IEventBus eventBus;
        private IInjectionRegistry injectionRegistry;

        private Builder() { }

        public Builder annotationManager(IWinryAnnotationManager annotationManager) {
            this.annotationManager = annotationManager;
            return this;
        }

        public Builder loggerFactory(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
            return this;
        }

        public Builder elementBootstrapper(IElementBootstrapper elementBootstrapper) {
            this.elementBootstrapper = elementBootstrapper;
            return this;
        }

        public Builder asyncManager(IWinryAsyncManager asyncManager) {
            this.asyncManager = asyncManager;
            return this;
        }

        public Builder eventBus(IEventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }

        public Builder injectionRegistry(IInjectionRegistry injectionRegistry) {
            this.injectionRegistry = injectionRegistry;
            return this;
        }

        public IWinryContext build() {
            if (Objects.isNull(this.bootstrap) || Objects.isNull(this.bootstrapper) || Objects.isNull(this.eventBus) ||
                Objects.isNull(this.asyncManager) || Objects.isNull(this.elementBootstrapper) || Objects.isNull(this.annotationManager) ||
                Objects.isNull(this.loggerFactory) || Objects.isNull(this.injectionRegistry)) {
                throw new IllegalStateException("Cannot build without all managers present!");
            }

            return new WinryContext(this);
        }
    }
}
