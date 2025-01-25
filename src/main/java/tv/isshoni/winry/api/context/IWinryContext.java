package tv.isshoni.winry.api.context;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.bootstrap.executable.IExecutable;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.List;

public interface IWinryContext {

    AraragiLogger createLogger(String name);

    AraragiLogger createLogger(Class<?> clazz);

    int getContextId();

    void registerToContext(Object object);

    void registerToContext(Object... objects);

    void addSingleton(Class<?> clazz) throws Throwable;

    void registerExecutable(IExecutable executable);

    void registerExecutable(IExecutable... executable);

    /**
     * Triggers a backload; reloads the accumulated "backlog" that is amassed through registration.
     * Generally used when you register a new annotation or service that you want to immediately use within
     * that same method
     */
    void backload();

    void reprocess(Class<? extends Annotation>... annotation);

    void suppressShutdown();

    void shutdown();

    Instant getCreated();

    IBootstrapContext getBootstrapContext();

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

    boolean hasSingleton(Class<?> clazz);

    default String getContextName() {
        return this.getBootstrapAnnotation().name();
    }

    default String getFileName() {
        return getContextName().toLowerCase().replaceAll(" ", "_");
    }
}
