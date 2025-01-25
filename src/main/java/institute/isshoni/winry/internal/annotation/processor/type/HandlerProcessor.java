package institute.isshoni.winry.internal.annotation.processor.type;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.winry.api.annotation.exception.Handler;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.exception.IExceptionHandler;
import institute.isshoni.winry.api.meta.IAnnotatedClass;

public class HandlerProcessor implements IWinryAnnotationProcessor<Handler> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public HandlerProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        this.LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void executeClass(IAnnotatedClass classMeta, Object target, Handler annotation) {
        if (!annotation.global()) {
            return;
        }

        if (!IExceptionHandler.class.isAssignableFrom(classMeta.getElement())) {
            return;
        }

        LOGGER.info("Discovered global exception handler: " + classMeta.getDisplay() + " -- " + annotation.value().getName());
        this.context.get().getExceptionManager().registerGlobal((Class<? extends IExceptionHandler<?>>) classMeta.getElement());
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
