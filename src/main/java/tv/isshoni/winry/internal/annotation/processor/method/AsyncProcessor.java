package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Async;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class AsyncProcessor implements IWinryAnnotationProcessor<Async> {

    private final AraragiLogger LOGGER;

    public AsyncProcessor(@Context IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("AsyncProcessor");
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Async annotation) {
        IAsyncManager asyncManager = getWinryContext().getAsyncManager();

        Method method = bootstrappedMethod.getBootstrappedElement();
        LOGGER.debug("Running on " + bootstrappedMethod);

        if (!(method.getReturnType().isAssignableFrom(Future.class) || method.getReturnType().equals(Void.TYPE))) {
            // TODO: Make specialized exception for this
            throw new RuntimeException("Tried to make async method with non-void/future return type!");
        }

        blueprint.registerSimpleMethodDelegator(bootstrappedMethod.getBootstrappedElement(), 0, (c, m, args, next) ->
                asyncManager.submit(() -> {
                    Object result = next.call();

                    if (result instanceof Future) {
                        return ((Future<?>) result).get();
                    }

                    return result;
                }));
    }
}
