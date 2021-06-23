package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class BootstrappedMethod implements IBootstrappedElement<Method> {

    private final IBootstrapper bootstrapper;

    private final AnnotationManager annotationManager;

    private final Method method;

    private final Collection<Annotation> annotations;

    private final Set<ReflectedModifier> modifiers;

    private boolean executed;

    public BootstrappedMethod(Method method, IBootstrapper bootstrapper) {
        this.method = method;
        this.bootstrapper = bootstrapper;
        this.annotationManager = bootstrapper.getAnnotationManager();
        this.modifiers = ReflectedModifier.getModifiers(method);
        this.annotations = this.annotationManager.getManagedAnnotationsOn(method);
        this.executed = false;

        if (this.annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.method.getName() + " has conflicting annotations! " + this.annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public Method getBootstrappedElement() {
        return this.method;
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
    public String getDisplay() {
        return this.method.toString();
    }

    public boolean isExecuted() {
        return this.executed;
    }

    public BootstrappedClass getDeclaringClass() {
        return this.bootstrapper.getElementBootstrapper().getDeclaringClass(this.method);
    }

    // TODO: maybe refactor these two methods to be a little less copy and pasted
    @Override
    public void execute() {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.executeMethod(this);
        }
    }

    @Override
    public void transform() {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.transformMethod(this, getDeclaringClass().getTransformingBlueprint());
        }
    }

    @Override
    public String toString() {
        return "BootstrappedMethod[method=" + this.method.getName() + ",weight=" + this.getWeight() + "]";
    }
}
