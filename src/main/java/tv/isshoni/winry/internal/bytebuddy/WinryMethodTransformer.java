package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static net.bytebuddy.implementation.MethodDelegation.to;

public class WinryMethodTransformer implements MethodTransformingPlan {

    private static AraragiLogger logger;

    private final List<Pair<MethodDelegator, Integer>> delegators;

    private final List<Function<DynamicType.Builder.MethodDefinition.ParameterDefinition, DynamicType.Builder.MethodDefinition.ParameterDefinition>> parameterTransformers;

    private boolean removeParameters = false;

    public WinryMethodTransformer(BootstrappedMethod method) {
        logger = method.getWinryContext().getLoggerFactory().createLogger("WinryMethodTransformer");

        this.delegators = new LinkedList<>();
        this.parameterTransformers = new LinkedList<>();
    }

    public void addDelegator(MethodDelegator delegator, int weight) {
        this.delegators.add(new Pair<>(delegator, weight));
    }

    public void addParameter(Function<DynamicType.Builder.MethodDefinition.ParameterDefinition, DynamicType.Builder.MethodDefinition.ParameterDefinition> transformer) {
        this.parameterTransformers.add(transformer);
    }

    public void removeParameters() {
        this.removeParameters = true;
    }

    public DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> buildMethod(Method element, DynamicType.Builder<?> builder) {
        DynamicType.Builder.MethodDefinition.ParameterDefinition<?> methodHeader = methodHeader(element, builder);

        if (this.parameterTransformers.isEmpty() && !this.removeParameters) {
            return parametersFrom(element, methodHeader);
        } else {
            return Streams.to(this.parameterTransformers)
                    .collapse(Function::apply, methodHeader);
        }
    }

    @Override
    public DynamicType.Builder<?> transform(Method element, BootstrappedMethod bootstrapped, DynamicType.Builder<?> builder) {
        return buildMethod(element, builder).intercept(to(new WinryBytebuddyDelegator(this.delegators)));
    }

    public static DynamicType.Builder.MethodDefinition.ParameterDefinition<?> methodHeader(Method element, DynamicType.Builder<?> builder) {
        logger.debug("Building method transformer: " + element.getName());
        return builder.defineMethod(element.getName(), element.getReturnType(), element.getModifiers());
    }

    public static DynamicType.Builder.MethodDefinition.ParameterDefinition<?> parametersFrom(Method element, DynamicType.Builder.MethodDefinition.ParameterDefinition<?> builder) {
        for (Parameter parameter : element.getParameters()) {
            builder = builder.withParameter(parameter.getParameterizedType(), parameter.getName(),
                            parameter.getModifiers())
                    .annotateParameter(parameter.getDeclaredAnnotations());
        }

        return builder;
    }

    public static DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> replicateMethod(Method element, DynamicType.Builder<?> builder) {
        DynamicType.Builder.MethodDefinition.ParameterDefinition<?> header = methodHeader(element, builder);

        return parametersFrom(element, header);
    }
}
