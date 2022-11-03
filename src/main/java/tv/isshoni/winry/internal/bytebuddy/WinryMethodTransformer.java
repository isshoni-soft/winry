package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
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
        if (!this.removeParameters) {
            return replicateMethod(element, builder);
        } else {
            return methodHeader(element, builder);
        }
    }

    @Override
    public DynamicType.Builder<?> transform(Method element, BootstrappedMethod bootstrapped, DynamicType.Builder<?> builder) {
        return buildMethod(element, builder).intercept(to(new WinryBytebuddyDelegator(this.delegators)));
    }

    public static DynamicType.Builder.MethodDefinition.ParameterDefinition<?> methodHeader(Method element, DynamicType.Builder<?> builder) {
        return builder.defineMethod(element.getName(), element.getGenericReturnType(), element.getModifiers());
    }

    public static DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> parametersFrom(Method element, DynamicType.Builder.MethodDefinition.ParameterDefinition<?> builder) {
        DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> result = builder;

        // TODO: Look into if reusing builder here will screw us when a method has multiple parameters.
        for (Parameter parameter : element.getParameters()) {
            result = attachGenericsToParameter(parameter, builder
                    .withParameter(parameter.getParameterizedType(), parameter.getName(), parameter.getModifiers())
                    .annotateParameter(parameter.getDeclaredAnnotations()));
        }

        return result;
    }

    public static DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> replicateMethod(Method element, DynamicType.Builder<?> builder) {
        logger.debug("Replicating method: " + element);
        return parametersFrom(element, methodHeader(element, builder));
    }

    public static DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> attachGenericsToParameter(Parameter parameter, DynamicType.Builder.MethodDefinition.TypeVariableDefinition<?> parameters) {
        Type pt = parameter.getParameterizedType();

        if (!(pt instanceof ParameterizedType)) {
            return parameters;
        }

        logger.debug("-> Found generic parameter: " + parameter);

        Type[] types = ((ParameterizedType) pt).getActualTypeArguments();

        logger.debug("-> Found actual types: " + Arrays.toString(types));

        for (Type actual : types) {
            parameters = parameters.typeVariable(actual.getTypeName(), actual);
        }

        return parameters;
    }
}
