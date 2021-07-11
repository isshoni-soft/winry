package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;

public class BasicClassProcessor implements IAnnotationProcessor<Annotation> {

    private final static WinryLogger LOGGER = WinryLogger.create("BasicClassProcessor");

    @Override
    public void executeClass(BootstrappedClass bootstrappedClass, Annotation annotation) {
        if (bootstrappedClass.isProvided()) {
            return;
        }

        if (bootstrappedClass.hasObject()) {
            LOGGER.warning("Two basic class processors present on type " + bootstrappedClass.getDisplay());
            return;
        }

        if (bootstrappedClass.hasWrappedClass()) {
            LOGGER.info("Produced wrapped class: " + bootstrappedClass.getWrappedClass().getName());
        }

        bootstrappedClass.setObject(bootstrappedClass.newInstance());
    }
}
