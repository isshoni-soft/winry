package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.annotation.Runner;

import java.lang.reflect.Method;
import java.util.Map;

public class BootstrappedMethod implements IBootstrappedElement<Runner, Method> {

    private final Method method;

    private final Runner runner;

    public BootstrappedMethod(Method method, Runner runner) {
        this.method = method;
        this.runner = runner;
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
    public int getWeight() {
        if (this.runner.weight() == Runner.DEFAULT_WEIGHT) {
            return this.runner.value().getWeight();
        }

        return this.runner.weight();
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {

    }

    @Override
    public String toString() {
        return "BootstrapMethod[method=" + this.method.getName() + ",runner=" + this.runner.value() + ",weight=" + this.getWeight() + "]";
    }
}
