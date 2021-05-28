package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BootstrappedMethod implements IBootstrappedElement<Method> {

    private final AnnotationManager annotationManager;

    private final Method method;

    private final Collection<Annotation> annotations;

    private final Set<ReflectedModifier> modifiers;

    public BootstrappedMethod(Method method, AnnotationManager annotationManager) {
        this.method = method;
        this.annotationManager = annotationManager;
        this.modifiers = ReflectedModifier.getModifiers(method);
        this.annotations = this.annotationManager.getManagedAnnotationsOn(method);

        if (this.annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.method.getName() + " has conflicting annotations! " + this.annotationManager.getConflictingAnnotations(this.annotations));
        }
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
        return ImmutableSet.copyOf(this.modifiers);
    }

    @Override
    public AnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

//    @Override
//    public int getWeight() {
//        Optional<Runner> runner = this.annotations.stream()
//                .map(a -> new Pair<Class<? extends Annotation>, Annotation>(a.annotationType(), a))
//                .filter(p -> p.getFirst().equals(Runner.class))
//                .map(p -> (Runner) p.getSecond())
//                .findFirst();
//
//        Optional<Integer> weight = runner.map(Runner::weight);
//
//        if (!runner.isPresent()) {
//            throw new IllegalStateException("Runner annotation not found!");
//        }
//
//        if (weight.get() == Runner.DEFAULT_WEIGHT) {
//            return runner.get().value().getWeight();
//        }
//
//        return weight.get();
//    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.executeMethod(this, provided);
        }
    }

    @Override
    public String toString() {
        return "BootstrappedMethod[method=" + this.method.getName() + ",weight=" + this.getWeight() + "]";
    }
}
