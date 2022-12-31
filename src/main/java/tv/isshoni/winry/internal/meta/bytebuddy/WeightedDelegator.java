package tv.isshoni.winry.internal.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.bytebuddy.IMethodDelegator;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class WeightedDelegator implements Comparable<WeightedDelegator> {

    private final int weight;

    private final IMethodDelegator delegator;

    public WeightedDelegator(int weight, IMethodDelegator delegator) {
        this.weight = weight;
        this.delegator = delegator;
    }

    public Object delegate(Object caller, Method original, Object[] args, Supplier<Object> nextCall) {
        return this.delegator.delegate(caller, original, args, nextCall);
    }

    public boolean isValid() {
        return this.delegator != null;
    }

    public int getWeight() {
        return this.weight;
    }

    public IMethodDelegator getDelegator() {
        return this.delegator;
    }

    @Override
    public int compareTo(WeightedDelegator o) {
        return Integer.compare(this.weight, o.weight);
    }
}
