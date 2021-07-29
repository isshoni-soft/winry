package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.context.IWinryContext;

import java.lang.annotation.Annotation;

public class BasicMethodProcessor implements IAnnotationProcessor<Annotation> {

    private final AraragiLogger LOGGER;

    public BasicMethodProcessor(IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("BasicMethodProcessor");
    }

    @Override
    public void executeMethod(BootstrappedMethod method, Annotation annotation) {
        if (method.isExecuted()) {
            LOGGER.warn("Tried to execute a method that has already been executed!");
            return;
        }

        LOGGER.debug("Executing: " + method.getDisplay());
        method.getBootstrapper().execute(method);
    }
}
