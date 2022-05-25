package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.Bootstrap;

import java.lang.annotation.Annotation;

public interface IWinryAnnotationManager extends IAnnotationManager {

    void initialize(Bootstrap bootstrap);

    boolean isWinry(IAnnotationProcessor<Annotation> processor);

    boolean isWinry(IPreparedAnnotationProcessor processor);
}
