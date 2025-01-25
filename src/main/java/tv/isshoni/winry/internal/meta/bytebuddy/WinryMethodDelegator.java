package tv.isshoni.winry.internal.meta.bytebuddy;

import institute.isshoni.araragi.stream.Streams;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IMethodDelegator;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IMethodTransformer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;

public class WinryMethodDelegator implements IMethodTransformer {

    private final IWinryContext context;

    private final Queue<WeightedDelegator> delegators;

    public WinryMethodDelegator(IWinryContext context) {
        this(context, new LinkedList<>());
    }

    public WinryMethodDelegator(IWinryContext context, List<WeightedDelegator> delegators) {
        this.context = context;
        this.delegators = new PriorityQueue<>(Comparable::compareTo);
        this.delegators.addAll(delegators);
    }

    @Override
    public DynamicType.Builder<?> transform(Method element, IAnnotatedMethod meta, DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.is(element)).intercept(MethodDelegation.to(this));
    }

    public void registerDelegator(WeightedDelegator delegator) {
        if (!delegator.isValid()) {
            return;
        }

        this.delegators.add(delegator);
    }

    public void registerDelegator(IMethodDelegator delegator, int weight) {
        registerDelegator(new WeightedDelegator(weight, delegator));
    }

    public void registerDelegators(WeightedDelegator... delegators) {
        Streams.to(delegators).forEach(this::registerDelegator);
    }

    public void registerDelegators(Collection<WeightedDelegator> delegators) {
        Streams.to(delegators).forEach(this::registerDelegator);
    }

    @RuntimeType
    public Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
        if (this.delegators.isEmpty()) {
            return this.context.getExceptionManager().unboxCallable(zuper, method).get();
        }

        return this.delegators.poll().delegate(object, method, args,
                this.context.getExceptionManager().unboxCallable(getNext(object, method, args, zuper), method));
    }

    private Callable<Object> getNext(Object object, Method method, Object[] args, Callable<Object> zuper) {
        if (this.delegators.isEmpty()) {
            return zuper;
        } else {
            return () -> this.delegators.poll().delegate(object, method, args,
                    this.context.getExceptionManager().unboxCallable(getNext(object, method, args, zuper), method));
        }
    }
}
