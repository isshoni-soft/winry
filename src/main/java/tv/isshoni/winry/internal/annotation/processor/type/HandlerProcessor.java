package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.exception.IExceptionHandler;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;

public class HandlerProcessor implements IWinryAnnotationProcessor<Handler> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public HandlerProcessor(@Context IWinryContext context) {
        this.context = context;
        this.LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void executeClass(BootstrappedClass bootstrappedClass, Handler annotation) {
        if (!annotation.global()) {
            return;
        }

        if (!IExceptionHandler.class.isAssignableFrom(bootstrappedClass.getBootstrappedElement())) {
            return;
        }

        LOGGER.info("Discovered global exception handler: " + bootstrappedClass.getBootstrappedElement().getName() + " -- " + annotation.value().getName());
        this.context.getExceptionManager().registerGlobal((Class<? extends IExceptionHandler<?>>) bootstrappedClass.getBootstrappedElement());
    }
}
