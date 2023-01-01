package tv.isshoni.winry.api.context;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.bootstrap.IExecutable;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.event.IEventBus;
import tv.isshoni.winry.internal.model.exception.IExceptionManager;
import tv.isshoni.winry.internal.model.logging.ILoggerFactory;
import tv.isshoni.winry.internal.model.meta.IInstanceManager;

import java.time.Instant;
import java.util.List;

public interface IWinryContext {

    AraragiLogger createLogger(String name);

    AraragiLogger createLogger(Class<?> clazz);

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

    IInstanceManager getInstanceManager();

    IWinryAsyncManager getAsyncManager();

    IEventBus getEventBus();

    IExceptionManager getExceptionManager();

    IMetaManager getMetaManager();

    List<IExecutable> getExecutables();

    Bootstrap getBootstrapAnnotation();

    default String getContextName() {
        return this.getBootstrapAnnotation().name();
    }

    default String getFileName() {
        return getContextName().toLowerCase().replaceAll(" ", "_");
    }
}
