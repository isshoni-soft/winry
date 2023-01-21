package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.ITransformable;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.reflect.Method;
import java.util.Set;

public class AnnotatedMethod extends AbstractAnnotatedMeta<Method> implements ITransformable<Method>, IAnnotatedMethod {

    protected final Set<ReflectedModifier> modifiers;

    protected final IAnnotatedClass parent;

    public AnnotatedMethod(IWinryContext context, IAnnotatedClass parent, Method method) {
        super(context, method);
        this.parent = parent;
        this.modifiers = ReflectedModifier.getModifiers(method.getModifiers());
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeMethod(this.parent.getInstance());
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getDisplay() {
        String result = "Method: " + this.element.getName();

        if (this.parent != null) {
            result += " (Parent " + this.parent.getElement().getSimpleName() + ")";
        }

        return result + " [" + this.annotations + "]";
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor, IWrapperGenerator generator) {
        preparedAnnotationProcessor.transformMethod(this, generator);
    }

    @Override
    public IAnnotatedClass getDeclaringClass() {
        return this.parent;
    }
}
