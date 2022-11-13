package tv.isshoni.winry.internal.entity.annotation.prepare;

import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class WinryPreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IWinryPreparedAnnotationProcessor<IWinryAnnotationProcessor<Annotation>> {

    public WinryPreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IWinryAnnotationProcessor<Annotation> processor, IAnnotationManager annotationManager) {
        super(annotation, element, processor, annotationManager);
    }

    @Override
    public IWinryAnnotationProcessor<Annotation> getProcessor() {
        return (IWinryAnnotationProcessor<Annotation>) this.getProcessorAs(IWinryAnnotationProcessor.class);
    }
}
