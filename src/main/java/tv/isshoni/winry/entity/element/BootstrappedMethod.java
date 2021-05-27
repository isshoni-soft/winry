package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class BootstrappedMethod implements IBootstrappedElement<Runner, Method> {

    private final Method method;

    private final Runner runner;

    private final Set<ReflectedModifier> modifiers;

    public BootstrappedMethod(Method method, Runner runner) {
        this.method = method;
        this.runner = runner;
        this.modifiers = ReflectedModifier.getModifiers(method);
    }

    @Override
    public Runner getAnnotation() {
        return this.runner;
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
        if (this.runner.weight() == Runner.DEFAULT_WEIGHT) {
            return this.runner.value().getWeight();
        }

        return this.runner.weight();
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
