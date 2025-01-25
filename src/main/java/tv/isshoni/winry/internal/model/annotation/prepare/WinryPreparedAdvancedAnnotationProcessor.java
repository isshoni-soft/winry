package tv.isshoni.winry.internal.model.annotation.prepare;

import institute.isshoni.araragi.annotation.manager.IAnnotationManager;
import institute.isshoni.araragi.annotation.processor.prepared.SimplePreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;

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
