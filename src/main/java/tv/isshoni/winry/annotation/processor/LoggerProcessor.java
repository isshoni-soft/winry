package tv.isshoni.winry.annotation.processor;

import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.reflect.Field;
import java.util.Map;

public class LoggerProcessor implements IAnnotationProcessor<Logger> {

    private static final WinryLogger LOGGER = WinryLogger.create("LoggerProcessor");

    @Override
    public void executeField(BootstrappedField bootstrappedField, Logger annotation, Map<Class<?>, Object> provided) {
        Field field = bootstrappedField.getBootstrappedElement();

        if (!field.getType().equals(WinryLogger.class)) {
            LOGGER.severe(bootstrappedField.getDisplay() + " is not of type WinryLogger, skipping...");
            return;
        }

        if (bootstrappedField.getModifiers().contains(ReflectedModifier.FINAL)) {
            LOGGER.severe(bootstrappedField.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
            return;
        }

        WinryLogger logger = ReflectionManager.executeMethod(WinryLogger.class, null, "create", annotation.value(), annotation.indent());

        LOGGER.info("Injecting: " + logger);
        ReflectionManager.injectField(bootstrappedField, logger);
    }
}
