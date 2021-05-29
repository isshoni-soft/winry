package tv.isshoni.winry.annotation.processor;

import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.element.BootstrappedMethod;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;

public class BasicMethodProcessor implements IAnnotationProcessor<Annotation> {

    private static final WinryLogger LOGGER = WinryLogger.create("BasicMethodProcessor");

    @Override
    public void executeMethod(BootstrappedMethod method, Annotation annotation) {
        if (method.isExecuted()) {
            LOGGER.warning("Tried to execute a method that has already been executed!");
            return;
        }

        LOGGER.info("Executing... " + method.getDisplay());
        method.getBootstrapper().execute(method);
    }
}
