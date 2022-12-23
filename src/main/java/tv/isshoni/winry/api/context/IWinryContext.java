package tv.isshoni.winry.api.context;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.bootstrap.IExecutable;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.annotation.inject.IInjectionRegistry;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.internal.model.event.IEventBus;
import tv.isshoni.winry.internal.model.exception.IExceptionManager;
import tv.isshoni.winry.internal.model.logging.ILoggerFactory;

import java.time.Instant;
import java.util.List;

public interface IWinryContext {

    int getContextId();

    void registerToContext(Object object);

    void registerToContext(Object... objects);

    void registerExecutable(IExecutable executable);

    void registerExecutable(IExecutable... executable);

    void suppressShutdown();

    void shutdown();

    Instant getCreated();

    IBootstrapper getBootstrapper();

    IWinryAnnotationManager getAnnotationManager();

    ILoggerFactory getLoggerFactory();

    IElementBootstrapper getElementBootstrapper();

    IWinryAsyncManager getAsyncManager();

    IEventBus getEventBus();

    IInjectionRegistry getInjectionRegistry();

    IExceptionManager getExceptionManager();

    List<IExecutable> getExecutables();

    Bootstrap getBootstrapAnnotation();

    default String getContextName() {
        return this.getBootstrapAnnotation().name();
    }

    default String getFileName() {
        return getContextName().toLowerCase().replaceAll(" ", "_");
    }
}
