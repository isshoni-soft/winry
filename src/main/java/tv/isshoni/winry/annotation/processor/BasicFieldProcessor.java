package tv.isshoni.winry.annotation.processor;

import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;

public class BasicFieldProcessor implements IAnnotationProcessor<Annotation> {

    private static final WinryLogger LOGGER = WinryLogger.create("BasicFieldProcessor");

    @Override
    public void executeField(BootstrappedField field, Annotation annotation) {
        if (!field.getTarget().hasObject()) {
            throw new IllegalStateException("Cannot inject a field into a target that is not instantiated!");
        }

        if (field.isInjected()) {
            LOGGER.warning("Attempted to re-inject field " + field.getDisplay() + "!");
            return;
        }

        LOGGER.info("Injecting: " + field.getTarget());
        field.getBootstrapper().inject(field);
    }
}