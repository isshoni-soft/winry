package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

public class ExceptionHandlerProcessor implements IWinryAnnotationProcessor<ExceptionHandler> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public ExceptionHandlerProcessor(@Context IWinryContext context) {
        this.context = context;
        this.LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void executeMethod(BootstrappedMethod method, ExceptionHandler annotation) {
        LOGGER.debug("Register ExceptionHandler for: " + annotation.value().getName() + " - " + method.getDisplay());
        this.context.getExceptionManager().registerMethod(method.getBootstrappedElement(), annotation);
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, MethodTransformingPlan methodPlan, ExceptionHandler annotation, ITransformingBlueprint blueprint) {
        if (blueprint.hasTransformers(bootstrappedMethod.getBootstrappedElement())) {
            return;
        }

        LOGGER.debug("Registering pass-through exception handler to: " + bootstrappedMethod.getDisplay());
        methodPlan.asWinry().ifPresentOrElse(mt ->
                mt.addDelegator((c, m, args, next) -> next.get(), Integer.MAX_VALUE),
                        NO_WINRY_METHOD_TRANSFORMER.apply(LOGGER));
    }
}
