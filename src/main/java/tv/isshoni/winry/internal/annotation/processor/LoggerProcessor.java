package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.reflect.Field;

public class LoggerProcessor implements IAnnotationProcessor<Logger> {

    private static final WinryLogger LOGGER = WinryLogger.create("LoggerProcessor");

    @Override
    public void executeField(BootstrappedField bootstrappedField, Logger annotation) {
        Field field = bootstrappedField.getBootstrappedElement();

        if (!field.getType().equals(WinryLogger.class)) {
            LOGGER.severe(bootstrappedField.getDisplay() + " is not of type WinryLogger, skipping...");
            return;
        }

        if (bootstrappedField.getModifiers().contains(ReflectedModifier.FINAL)) {
            LOGGER.severe(bootstrappedField.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
            return;
        }

        String name = annotation.value();

        if (name.equals(Logger.DEFAULT)) {
            name = bootstrappedField.getDeclaringClass().getDisplay();
        }

        WinryLogger logger = WinryLogger.create(name, annotation.indent());

        LOGGER.info("Injecting: " + logger);
        bootstrappedField.getBootstrapper().inject(bootstrappedField, logger);
    }
}
