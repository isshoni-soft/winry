package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.util.Pair;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BootstrappedMethod implements IBootstrappedElement<Method> {

    private final Method method;

    private final Collection<Annotation> annotations;

    private final Set<ReflectedModifier> modifiers;

    public BootstrappedMethod(Method method, Collection<Annotation> annotations) {
        this.method = method;
        this.annotations = annotations;
        this.modifiers = ReflectedModifier.getModifiers(method);
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
    public int getWeight() {
        List<Pair<Class<? extends Annotation>, Annotation>> annotationTypes = this.annotations.stream()
                .map(a -> new Pair<Class<? extends Annotation>, Annotation>(a.annotationType(), a))
                .collect(Collectors.toList());

        Runner runner = null;
        for (Pair<Class<? extends Annotation>, Annotation> pair : annotationTypes) {
            if (pair.getFirst().equals(Runner.class)) {
                runner = (Runner) pair.getSecond();
                break;
            }
        }

        if (runner == null) {
            throw new IllegalStateException("Runner annotation not found!");
        }

        if (runner.weight() == Runner.DEFAULT_WEIGHT) {
            return runner.value().getWeight();
        }

        return runner.weight();
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        ReflectionManager.executeMethod(this);
    }

    @Override
    public String toString() {
        return "BootstrappedMethod[method=" + this.method.getName() + ",runner=" + this.runner.value() + ",weight=" + this.getWeight() + "]";
    }
}
