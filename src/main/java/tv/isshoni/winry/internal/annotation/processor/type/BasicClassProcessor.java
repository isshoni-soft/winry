package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;

public class BasicClassProcessor implements IWinryAnnotationProcessor<Annotation> {

    private static AraragiLogger LOGGER;

    public BasicClassProcessor(@Context IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("BasicClassProcessor");
    }

    @Override
    public void executeClass(BootstrappedClass bootstrappedClass, Annotation annotation) {
        if (bootstrappedClass.isProvided()) {
            return;
        }

        if (bootstrappedClass.hasObject()) {
            LOGGER.warn("Two basic class processors present on type " + bootstrappedClass.getDisplay());
            return;
        }

        if (bootstrappedClass.hasWrappedClass()) {
            LOGGER.debug("Produced wrapped class: " + bootstrappedClass.getWrappedClass().getName());
        }

        Object instance = bootstrappedClass.newInstance();

        bootstrappedClass.getBootstrapper().getContext().registerToContext(instance);

        bootstrappedClass.setObject(instance);
    }
}
