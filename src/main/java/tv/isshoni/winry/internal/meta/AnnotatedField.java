package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.ITransformable;

import java.lang.reflect.Field;
import java.util.Set;

public class AnnotatedField extends AbstractAnnotatedMeta<Field> implements ITransformable<Field> {

    protected final Set<ReflectedModifier> modifiers;

    protected final AnnotatedClass parent;

    protected boolean transformed;

    public AnnotatedField(IWinryContext context, AnnotatedClass parent, Field field) {
        super(context, field);
        this.modifiers = ReflectedModifier.getModifiers(field);
        this.parent = parent;
    }

    @Override
    public String getDisplay() {
        return this.element.getName();
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeField(this.element);
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor) {


        this.transformed = true;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public boolean isTransformed() {
        return this.transformed;
    }
}
