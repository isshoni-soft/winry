package tv.isshoni.winry.internal.meta;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;

public class AnnotatedField extends AbstractAnnotatedMeta<Field> implements IAnnotatedField {

    protected final Set<ReflectedModifier> modifiers;

    protected final IAnnotatedClass parent;

    protected final Object parentInstance;

    public AnnotatedField(IWinryContext context, IAnnotatedClass parent, Object parentInstance, Field field) {
        super(context, field);
        this.modifiers = ReflectedModifier.getModifiers(field);
        this.parent = parent;
        this.parentInstance = parentInstance;
    }

    @Override
    public String getDisplay() {
        String result = "Field: " + this.element.getType().getSimpleName() + " " + this.element.getName();

        if (this.parent != null) {
            result += " (Parent " + this.parent.getElement().getSimpleName() + ")";
        }

        return result + " [" + this.annotations + "]";
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor, IWrapperGenerator generator) {
        preparedAnnotationProcessor.transformField(this,  generator);
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor, Object target) {
        preparedAnnotationProcessor.executeField(target);
    }

    @Override
    public IAnnotatedClass getDeclaringClass() {
        return this.parent;
    }

    @Override
    public Class<?> getType() {
        return this.element.getType();
    }

    @Override
    public Type getGenericType() {
        return this.element.getGenericType();
    }

    @Override
    public Object getDeclaringClassInstance() {
        return this.parentInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IAnnotatedField other)) {
            return false;
        }

        return other.getElement().equals(this.element) && other.getDeclaringClass().equals(this.parent);
    }

    @Override
    public int hashCode() {
        return this.element.hashCode() + this.parent.hashCode();
    }
}
