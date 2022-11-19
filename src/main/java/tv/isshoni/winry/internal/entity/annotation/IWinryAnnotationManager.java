package tv.isshoni.winry.internal.entity.annotation;

import tv.isshoni.araragi.annotation.manager.IAnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.internal.entity.bootstrap.IExecutableProvider;

import java.lang.annotation.Annotation;

public interface IWinryAnnotationManager extends IAnnotationManager {

    void initialize(Bootstrap bootstrap);

    String[] getAllLoadedPackages(Bootstrap bootstrap);

    Class<?>[] getAllManuallyLoaded(Bootstrap bootstrap);

    Class<? extends IExecutableProvider>[] getAllProviders(Bootstrap bootstrap);

    boolean isWinry(IAnnotationProcessor<Annotation> processor);

    boolean isWinry(IPreparedAnnotationProcessor processor);
}
