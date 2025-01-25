package tv.isshoni.winry.internal.annotation.processor;

import institute.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;

public class DummyProcessor implements IWinryAnnotationProcessor<Annotation> {

    @Override
    public Constant<IWinryContext> getContext() {
        return new Constant<>();
    }
}
