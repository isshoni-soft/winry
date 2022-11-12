package tv.isshoni.winry.internal.entity.annotation.prepare;

import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class WinryPreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IWinryPreparedAnnotationProcessor<IWinryAnnotationProcessor<Annotation>> {

    public WinryPreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IWinryAnnotationProcessor<Annotation> processor) {
        super(annotation, element, processor);
    }

    @Override
    public IWinryAnnotationProcessor<Annotation> getProcessor() {
        return (IWinryAnnotationProcessor<Annotation>) this.getProcessorAs(IWinryAnnotationProcessor.class);
    }
}
