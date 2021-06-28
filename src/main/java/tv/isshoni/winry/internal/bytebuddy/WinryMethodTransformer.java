package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class WinryMethodTransformer implements MethodTransformingPlan {

    private final List<Pair<MethodDelegator, Integer>> delegators;

    public WinryMethodTransformer() {
        this.delegators = new LinkedList<>();
    }

    public void addDelegator(MethodDelegator delegator, int weight) {
        this.delegators.add(new Pair<>(delegator, weight));
    }

    @Override
    public DynamicType.Builder<?> transform(Method element, BootstrappedMethod bootstrapped, DynamicType.Builder<?> builder) {
        return builder.method(named(element.getName())
                    .and(isDeclaredBy(element.getDeclaringClass()))
                    .and(returns(element.getReturnType())))
                .intercept(to(new WinryBytebuddyDelegator(this.delegators)));
    }
}
