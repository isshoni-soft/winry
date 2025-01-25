package institute.isshoni.winry.internal.annotation.processor;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;

public class DummyProcessor implements IWinryAnnotationProcessor<Annotation> {

    @Override
    public Constant<IWinryContext> getContext() {
        return new Constant<>();
    }
}
