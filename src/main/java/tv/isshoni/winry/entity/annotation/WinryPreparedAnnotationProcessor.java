package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;

import java.lang.annotation.Annotation;

public class WinryPreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IWinryPreparedAnnotationProcessor {

    public WinryPreparedAnnotationProcessor(Annotation annotation, IWinryAnnotationProcessor<Annotation> processor) {
        super(annotation, processor);
    }

    @Override
    public IWinryAnnotationProcessor<Annotation> getProcessor() {
        return (IWinryAnnotationProcessor<Annotation>) this.getProcessorAs(IWinryAnnotationProcessor.class);
    }
}
