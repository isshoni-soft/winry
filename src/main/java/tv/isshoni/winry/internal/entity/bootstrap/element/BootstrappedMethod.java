package tv.isshoni.winry.internal.entity.bootstrap.element;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.IBootstrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BootstrappedMethod implements IBootstrappedElement<Method>, IContextual {

    private final IBootstrapper bootstrapper;

    private final Method method;

    private final List<Annotation> annotations;

    private final Set<ReflectedModifier> modifiers;

    private boolean executed;

    public BootstrappedMethod(Method method, IBootstrapper bootstrapper) {
        IWinryAnnotationManager annotationManager = bootstrapper.getContext().getAnnotationManager();

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
    public List<Annotation> getAnnotations() {
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
    public String getSimpleName() {
        return "Method";
    }

    public boolean isExecuted() {
        return this.executed;
    }

    public BootstrappedClass getDeclaringClass() {
        return this.bootstrapper.getContext().getElementBootstrapper().getDeclaringClass(this.method);
    }

    @Override
    public Consumer<IPreparedAnnotationProcessor> executeClass() {
        return (processor) -> processor.executeMethod(this.getBootstrappedElement());
    }

    @Override
    public Consumer<IWinryPreparedAnnotationProcessor> transformClass() {
        return (processor) -> processor.transformMethod(this, getDeclaringClass().getTransformingBlueprint());
    }

    @Override
    public String toString() {
        return "BootstrappedMethod[method=" + this.method.getName() + ",weight=" + this.getWeight() + "]";
    }
}