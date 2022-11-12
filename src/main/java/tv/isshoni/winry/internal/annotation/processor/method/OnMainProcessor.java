package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.transformer.OnMain;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.async.IWinryAsyncManager;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class OnMainProcessor implements IWinryAnnotationProcessor<OnMain> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public OnMainProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = context.getLoggerFactory().createLogger("OnMainProcessor");
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, MethodTransformingPlan methodPlan, OnMain annotation, ITransformingBlueprint blueprint) {
        IWinryAsyncManager asyncManager = this.context.getAsyncManager();

        Method method = bootstrappedMethod.getBootstrappedElement();
        LOGGER.debug("OnMain-ifying: " + bootstrappedMethod);

        if (!(method.getReturnType().isAssignableFrom(Future.class) || method.getReturnType().equals(Void.TYPE))) {
            // TODO: Make specialized exception for this
            throw new RuntimeException("Tried to make on main method with non-void/future return type!");
        }

        methodPlan.asWinry().ifPresentOrElse(mt ->
                mt.addDelegator((c, m, args, next) ->
                        asyncManager.submitToMain(() -> {
                            Object result = next.call();

                            if (result instanceof Future) {
                                return ((Future<?>) result).get();
                            }

                            return result;
                        }), 0), NO_WINRY_METHOD_TRANSFORMER.apply(LOGGER));
    }
}
