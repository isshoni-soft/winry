package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.model.bytebuddy.MethodDelegator;
import tv.isshoni.winry.internal.model.bytebuddy.MethodTransformingPlan;
import tv.isshoni.winry.internal.model.exception.IExceptionManager;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class WinryMethodTransformer implements MethodTransformingPlan {

    private static AraragiLogger logger;

    private final IExceptionManager exceptionManager;

    private final List<Pair<MethodDelegator, Integer>> delegators;

    private final List<Function<DynamicType.Builder.MethodDefinition.ParameterDefinition, DynamicType.Builder.MethodDefinition.ParameterDefinition>> parameterTransformers;

    private boolean removeParameters = false;

    public WinryMethodTransformer(BootstrappedMethod method, IExceptionManager exceptionManager) {
        logger = method.getWinryContext().getLoggerFactory().createLogger("WinryMethodTransformer");

        this.exceptionManager = exceptionManager;
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

    public boolean hasDelegators() {
        return this.delegators.size() > 0;
    }

    @Override
    public DynamicType.Builder<?> transform(Method element, BootstrappedMethod bootstrapped, DynamicType.Builder<?> builder) {
        logger.debug("Transforming: " + element);
        DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> method;
        if (this.removeParameters) {
            logger.debug("-> Removing parameters...");
            method = builder.defineMethod(element.getName(), element.getGenericReturnType(), element.getModifiers());
        } else {
            method = builder.method(ElementMatchers.is(element));
        }

        return method.intercept(MethodDelegation.to(new WinryBytebuddyDelegator(this.delegators, this.exceptionManager)));
    }
}
