package tv.isshoni.winry.api.entity.context;

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
import java.util.List;

public interface IWinryContext {

    int getContextId();

    void registerToContext(Object object);

    void registerToContext(Object... objects);

    void registerExecutable(IExecutable executable);

    void registerExecutable(IExecutable... executable);

    Instant getCreated();

    IBootstrapper getBootstrapper();

    IWinryAnnotationManager getAnnotationManager();

    ILoggerFactory getLoggerFactory();

    IElementBootstrapper getElementBootstrapper();

    IWinryAsyncManager getAsyncManager();

    IEventBus getEventBus();

    IInjectionRegistry getInjectionRegistry();

    List<IExecutable> getExecutables();

    Bootstrap getBootstrapAnnotation();

    default String getContextName() {
        return this.getBootstrapAnnotation().name();
    }

    default String getFileName() {
        return getContextName().toLowerCase().replaceAll(" ", "_");
    }
}
