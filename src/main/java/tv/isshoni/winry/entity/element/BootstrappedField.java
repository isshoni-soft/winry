package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

public class BootstrappedField implements IBootstrappedElement<Field> {

    private final IBootstrapper bootstrapper;

    private final AnnotationManager annotationManager;

    private final Field field;

    private final Set<ReflectedModifier> modifiers;

    private final Collection<Annotation> annotations;

    private final BootstrappedClass target;

    private boolean injected = false;

    public BootstrappedField(Field field, BootstrappedClass target, IBootstrapper bootstrapper) {
        this.field = field;
        this.bootstrapper = bootstrapper;
        this.target = target;
        this.annotationManager = bootstrapper.getAnnotationManager();
        this.modifiers = ReflectedModifier.getModifiers(field);
        this.annotations = this.annotationManager.getManagedAnnotationsOn(field);

        if (this.annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.field.getName() + " has conflicting annotations! " + this.annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public Field getBootstrappedElement() {
        return this.field;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifiers);
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    public BootstrappedClass getTarget() {
        return this.target;
    }

    public boolean isInjected() {
        return this.injected;
    }

    @Override
    public int getWeight() {
        if (this.target != null && this.target.isProvided()) {
            return 8; // TODO: Revisit these values
        }

        return IBootstrappedElement.super.getWeight();
    }

    @Override
    public void execute() {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.executeField(this);
        }
    }

    @Override
    public String getDisplay() {
        return ReflectedModifier.toString(this.field) + " " + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName();
    }

    @Override
    public String toString() {
        return "BootstrappedField[field=" + this.field.getName() + ",target=" + (this.target != null ? this.target.getBootstrappedElement().getName() : "null") + ",weight=" + this.getWeight() + "]";
    }
}
