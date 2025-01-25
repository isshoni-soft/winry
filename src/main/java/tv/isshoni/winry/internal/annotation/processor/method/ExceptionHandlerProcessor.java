package tv.isshoni.winry.internal.annotation.processor.method;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

public class ExceptionHandlerProcessor implements IWinryAnnotationProcessor<ExceptionHandler> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public ExceptionHandlerProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        this.LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void executeMethod(IAnnotatedMethod method, Object target, ExceptionHandler annotation) {
        LOGGER.debug("Register ExceptionHandler for: " + annotation.value().getName() + " - " + method.getDisplay());
        this.context.get().getExceptionManager().registerMethod(method.getElement(), annotation);
    }

    @Override
    public void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator, ExceptionHandler annotation) {
        if (generator.hasTransformer(method)) {
            return;
        }

        LOGGER.debug("Registering pass-through exception handler to: " + method.getDisplay());
        generator.delegateMethod(method, 0, (c, m, args, next) -> next.get());
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
