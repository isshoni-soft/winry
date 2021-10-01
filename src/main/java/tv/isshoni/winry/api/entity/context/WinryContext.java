package tv.isshoni.winry.api.entity.context;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private final IAsyncManager asyncManager;

    private final List<IExecutable> executables;

    private final Bootstrap bootstrap;

    public WinryContext(IBootstrapper bootstrapper, IAsyncManager asyncManager, IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory, IElementBootstrapper elementBootstrapper, Bootstrap bootstrap) {
        this.id = CONTEXT_ID.getAndIncrement();
        this.created = Instant.now();
        this.bootstrapper = bootstrapper;
        this.annotationManager = annotationManager;
        this.loggerFactory = loggerFactory;
        this.elementBootstrapper = elementBootstrapper;
        this.bootstrap = bootstrap;
        this.asyncManager = asyncManager;
        this.executables = new LinkedList<>();
        this.logger = loggerFactory.createLogger("WinryContext [" + this.id + "]");

        registerToContext(this.bootstrapper);
        registerToContext(this.annotationManager);
        registerToContext(this.loggerFactory);
        registerToContext(this.elementBootstrapper);
        registerToContext(this.bootstrap);
        registerToContext(this.asyncManager);

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
    public IAsyncManager getAsyncManager() {
        return this.asyncManager;
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
}
