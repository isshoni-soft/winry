package tv.isshoni.winry.internal.model.annotation;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.manager.IAnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.bootstrap.IExecutableProvider;
import tv.isshoni.winry.api.context.IExceptionManager;

import java.lang.annotation.Annotation;

public interface IWinryAnnotationManager extends IAnnotationManager {

    void initialize();

    Bootstrap getBootstrap();

    String[] getAllLoadedPackages(Bootstrap bootstrap);

    String[] getAllLoadedPackages();

    Class<?>[] getAllManuallyLoaded(Bootstrap bootstrap);

    Class<?>[] getAllManuallyLoaded();

    IAnnotationDiscoverer getAnnotationDiscoverer();

    IExceptionManager getExceptionManager();

    IBootstrapper getBootstrapper();

    Class<? extends IExecutableProvider>[] getAllProviders(Bootstrap bootstrap);

    Class<? extends IExecutableProvider>[] getAllProviders();

    boolean isWinry(IAnnotationProcessor<Annotation> processor);

    boolean isWinry(IPreparedAnnotationProcessor processor);
}
