package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.api.meta.IAnnotatedClass;

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
