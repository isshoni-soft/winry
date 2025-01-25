package institute.isshoni.winry.internal.model.annotation.prepare;

import institute.isshoni.araragi.annotation.manager.IAnnotationManager;
import institute.isshoni.araragi.annotation.processor.prepared.SimplePreparedAnnotationProcessor;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;

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
