package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.transformer.OnMain;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
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
                mt.addDelegator((c, m, args, next) -> {
                    try {
                        return asyncManager.submitToMain(() -> {
                            Object result = next.get();

                            if (result instanceof Future) {
                                return ((Future<?>) result).get();
                            }

                            return result;
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }, 0), NO_WINRY_METHOD_TRANSFORMER.apply(LOGGER));
    }
}
