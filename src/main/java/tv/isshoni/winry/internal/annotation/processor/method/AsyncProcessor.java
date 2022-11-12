package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.transformer.Async;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class AsyncProcessor implements IWinryAnnotationProcessor<Async> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public AsyncProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = context.getLoggerFactory().createLogger("AsyncProcessor");
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, MethodTransformingPlan methodPlan, Async annotation, ITransformingBlueprint blueprint) {
        IAsyncManager asyncManager = this.context.getAsyncManager();

        Method method = bootstrappedMethod.getBootstrappedElement();
        LOGGER.debug("Async-ifying: " + bootstrappedMethod);

        if (!(method.getReturnType().isAssignableFrom(Future.class) || method.getReturnType().equals(Void.TYPE))) {
            // TODO: Make specialized exception for this
            throw new RuntimeException("Tried to make async method with non-void/future return type!");
        }

        methodPlan.asWinry().ifPresentOrElse(mt ->
                mt.addDelegator((c, m, args, next) ->
                        asyncManager.submit(() -> {
                            Object result = next.call();

                            if (result instanceof Future) {
                                return ((Future<?>) result).get();
                            }

                            return result;
                        }), 0), NO_WINRY_METHOD_TRANSFORMER.apply(LOGGER));
    }
}
