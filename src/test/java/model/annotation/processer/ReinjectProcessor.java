package model.annotation.processer;

import model.annotation.Reinject;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedField;

public class ReinjectProcessor implements IWinryAdvancedAnnotationProcessor<Reinject, Integer> {

    private static int num = 0;

    private Constant<IWinryContext> context;

    public ReinjectProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
    }

    @Override
    public void executeField(IAnnotatedField field, Object target, Reinject annotation) {
        this.context.get().getMetaManager().inject(field, target, num++);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
