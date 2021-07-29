package tv.isshoni.winry.internal.context;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WinryContext implements IWinryContext {

    private static final Map<Object, WinryContext> CONTEXT_REGISTRY = new ConcurrentHashMap<>();

    public static Optional<WinryContext> getContextFor(Object object) {
        return Optional.ofNullable(CONTEXT_REGISTRY.get(object));
    }

    public static Map<Object, WinryContext> getContextRegistry() {
        return Collections.unmodifiableMap(CONTEXT_REGISTRY);
    }

    private final IBootstrapper bootstrapper;

    private final IAnnotationManager annotationManager;

    private final ILoggerFactory loggerFactory;

    private final IElementBootstrapper elementBootstrapper;

    private final IAsyncManager asyncManager;

    private final Bootstrap bootstrap;

    public WinryContext(IBootstrapper bootstrapper, IAsyncManager asyncManager, IAnnotationManager annotationManager, ILoggerFactory loggerFactory, IElementBootstrapper elementBootstrapper, Bootstrap bootstrap) {
        this.bootstrapper = bootstrapper;
        this.annotationManager = annotationManager;
        this.loggerFactory = loggerFactory;
        this.elementBootstrapper = elementBootstrapper;
        this.bootstrap = bootstrap;
        this.asyncManager = asyncManager;

        register(this.bootstrapper);
        register(this.annotationManager);
        register(this.loggerFactory);
        register(this.elementBootstrapper);
        register(this.bootstrap);
        register(this.asyncManager);
    }

    @Override
    public void register(Object object) {
        CONTEXT_REGISTRY.putIfAbsent(object, this);
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    @Override
    public IAnnotationManager getAnnotationManager() {
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
    public Bootstrap getBootstrapAnnotation() {
        return this.bootstrap;
    }

    @Override
    public String toString() {
        return "WinryContext[" + this.bootstrapper.getClass().getName() + "]";
    }
}
