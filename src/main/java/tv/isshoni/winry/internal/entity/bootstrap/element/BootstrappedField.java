package tv.isshoni.winry.internal.entity.bootstrap.element;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.util.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BootstrappedField implements IBootstrappedElement<Field>, IContextual {

    private final IBootstrapper bootstrapper;

    private final Field field;

    private final Set<ReflectedModifier> modifiers;

    private final List<Annotation> annotations;

    private final BootstrappedClass target;

    private boolean injected = false;

    public BootstrappedField(Field field, BootstrappedClass target, IBootstrapper bootstrapper) {
        IWinryAnnotationManager annotationManager = bootstrapper.getContext().getAnnotationManager();

        this.field = field;
        this.bootstrapper = bootstrapper;
        this.target = target;
        this.modifiers = ReflectedModifier.getModifiers(field);
        this.annotations = annotationManager.getManagedAnnotationsOn(field);

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.field.getName() + " has conflicting annotations! " + annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public Field getBootstrappedElement() {
        return this.field;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    @Override
    public String getSimpleName() {
        return "Field";
    }

    public BootstrappedClass getTarget() {
        return this.target;
    }

    public BootstrappedClass getDeclaringClass() {
        return this.bootstrapper.getContext().getElementBootstrapper().getDeclaringClass(this.field);
    }

    public boolean isInjected() {
        return this.injected;
    }

    @Override
    public int getWeight() {
        if (this.target != null && this.target.isProvided()) {
            return 1000000;
        }

        return IBootstrappedElement.super.getWeight();
    }

    @Override
    public Consumer<IPreparedAnnotationProcessor> executeClass() {
        return (processor) -> processor.executeField(this.getBootstrappedElement());
    }

    @Override
    public Consumer<IWinryPreparedAnnotationProcessor> transformClass() {
        return (processor) -> processor.transformField(this, getDeclaringClass().getTransformingBlueprint());
    }

    @Override
    public String toString() {
        return "BootstrappedField[field=" + this.field.getName() + ",target=" + (this.target != null ? this.target.getBootstrappedElement().getName() : "null") + ",weight=" + this.getWeight() + "]";
    }
}
