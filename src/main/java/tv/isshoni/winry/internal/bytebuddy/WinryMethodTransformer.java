package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.*;

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
        return replicateMethod(element, builder).intercept(to(new WinryBytebuddyDelegator(this.delegators)));
    }

    public static DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> replicateMethod(Method element, DynamicType.Builder<?> builder) {
        DynamicType.Builder.MethodDefinition.ParameterDefinition<?> methodBuilder =
                builder.defineMethod(element.getName(), element.getReturnType(), element.getModifiers());

        for (Parameter parameter : element.getParameters()) {
            methodBuilder = methodBuilder.withParameter(parameter.getType(), parameter.getName(), parameter.getModifiers())
                    .annotateParameter(parameter.getDeclaredAnnotations());
        }

        return methodBuilder;
    }
}
