package tv.isshoni.winry.internal.entity.annotation.prepare;

import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;
import tv.isshoni.winry.internal.entity.annotation.IWinryAdvancedAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class WinryPreparedAdvancedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements
        IWinryPreparedAdvancedAnnotationProcessor {

    public WinryPreparedAdvancedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IWinryAdvancedAnnotationProcessor<Annotation, Object> processor, IAnnotationManager annotationManager) {
        super(annotation, element, processor, annotationManager);
    }

    @Override
    public IWinryAdvancedAnnotationProcessor<Annotation, Object> getProcessor() {
        return (IWinryAdvancedAnnotationProcessor<Annotation, Object>) this.getProcessorAs(IWinryAdvancedAnnotationProcessor.class);
    }
}
