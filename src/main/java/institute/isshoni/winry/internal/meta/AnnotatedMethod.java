package institute.isshoni.winry.internal.meta;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.araragi.reflect.ReflectedModifier;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import institute.isshoni.winry.internal.model.meta.ITransformable;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.reflect.Method;
import java.util.Set;

public class AnnotatedMethod extends AbstractAnnotatedMeta<Method> implements ITransformable<Method>, IAnnotatedMethod {

    protected final Set<ReflectedModifier> modifiers;

    protected final IAnnotatedClass parent;

    protected final Object parentInstance;

    public AnnotatedMethod(IWinryContext context, IAnnotatedClass parent, Object parentInstance, Method method) {
        super(context, method);
        this.parent = parent;
        this.parentInstance = parentInstance;
        this.modifiers = ReflectedModifier.getModifiers(method.getModifiers());
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
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor, Object target) {
        preparedAnnotationProcessor.executeMethod(target);
    }

    @Override
    public IAnnotatedClass getDeclaringClass() {
        return this.parent;
    }

    @Override
    public Object getDeclaringClassInstance() {
        return this.parentInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IAnnotatedMethod other)) {
            return false;
        }

        return other.getElement().equals(this.element) && other.getDeclaringClass().equals(this.parent);
    }

    @Override
    public int hashCode() {
        return this.element.hashCode() + this.parent.hashCode();
    }

    @Override
    public Class<?> getReturnType() {
        return this.element.getReturnType();
    }
}
