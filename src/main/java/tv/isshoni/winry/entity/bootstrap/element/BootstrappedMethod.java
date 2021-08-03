package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.WinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.context.IContextual;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class BootstrappedMethod implements IBootstrappedElement<Method>, IContextual {

    private final IBootstrapper bootstrapper;

    private final Method method;

    private final Collection<Annotation> annotations;

    private final Set<ReflectedModifier> modifiers;

    private boolean executed;

    public BootstrappedMethod(Method method, IBootstrapper bootstrapper) {
        IWinryAnnotationManager annotationManager = bootstrapper.getAnnotationManager();

        this.method = method;
        this.bootstrapper = bootstrapper;
        this.modifiers = ReflectedModifier.getModifiers(method);
        this.annotations = annotationManager.getManagedAnnotationsOn(method);
        this.executed = false;

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.method.getName() + " has conflicting annotations! " + annotationManager.getConflictingAnnotations(this.annotations));
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

    @Override
    public Consumer<WinryPreparedAnnotationProcessor> executeClass() {
        return (processor) -> processor.executeMethod(this);
    }

    @Override
    public Consumer<WinryPreparedAnnotationProcessor> transformClass() {
        return (processor) -> processor.transformMethod(this, getDeclaringClass().getTransformingBlueprint());
    }

    @Override
    public String toString() {
        return "BootstrappedMethod[method=" + this.method.getName() + ",weight=" + this.getWeight() + "]";
    }
}
