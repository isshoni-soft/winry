package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;

public class BasicMethodProcessor implements IWinryAnnotationProcessor<Annotation> {

    private final AraragiLogger LOGGER;

    public BasicMethodProcessor(@Context IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("BasicMethodProcessor");
    }

    @Override
    public void executeMethod(BootstrappedMethod method, Annotation annotation) {
        if (method.isExecuted()) {
            LOGGER.warn("Tried to execute a method that has already been executed!");
            return;
        }

        LOGGER.debug("Executing: " + method.getDisplay());
        method.getBootstrapper().getContext().getElementBootstrapper().execute(method);
    }
}
