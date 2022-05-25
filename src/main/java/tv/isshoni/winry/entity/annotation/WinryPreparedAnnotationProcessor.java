package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class WinryPreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IWinryPreparedAnnotationProcessor {

    public WinryPreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IWinryAnnotationProcessor<Annotation> processor) {
        super(annotation, element, processor);
    }

    @Override
    public IWinryAnnotationProcessor<Annotation> getProcessor() {
        return (IWinryAnnotationProcessor<Annotation>) this.getProcessorAs(IWinryAnnotationProcessor.class);
    }
}
