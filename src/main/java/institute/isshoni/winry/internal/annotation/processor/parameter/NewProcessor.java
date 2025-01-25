package institute.isshoni.winry.internal.annotation.processor.parameter;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.parameter.New;
import institute.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.service.ObjectFactory;

import java.lang.reflect.Parameter;

public class NewProcessor implements IWinryAdvancedAnnotationProcessor<New, Object> {

    private final Constant<IWinryContext> context;

    private final ObjectFactory objectFactory;

    public NewProcessor(@Context IWinryContext context, @Inject ObjectFactory objectFactory) {
        this.context = new Constant<>(context);
        this.objectFactory = objectFactory;
    }

    @Override
    public Object supply(New annotation, Object previous, Parameter parameter) {
        return this.objectFactory.construct(parameter.getType());
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
