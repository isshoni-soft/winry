package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.annotation.transformer.OnMain;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

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

        LOGGER.debug("Applying transformation to: " + bootstrappedMethod);

        methodPlan.asWinry().ifPresentOrElse(mt ->
                mt.addDelegator((c, m, args, next) -> {
                    try {
                        Future<?> onMain = asyncManager.submitToMain(() -> {
                            Object result = next.get();

                            if (result instanceof Future) {
                                return ((Future<?>) result).get();
                            }

                            return result;
                        });

                        if (Future.class.isAssignableFrom(m.getReturnType()) ||
                                (m.getReturnType().equals(Void.TYPE) && !annotation.block())) {
                            return onMain;
                        }

                        return onMain.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw Exceptions.rethrow(e);
                    }
                }, 0), NO_WINRY_METHOD_TRANSFORMER.apply(LOGGER));
    }
}
