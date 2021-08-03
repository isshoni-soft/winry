package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.reflect.Field;

public class LoggerProcessor implements IWinryAnnotationProcessor<Logger> {

    private final AraragiLogger LOGGER;

    public LoggerProcessor(IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("LoggerProcessor");
    }

    @Override
    public void executeField(BootstrappedField bootstrappedField, Logger annotation) {
        Field field = bootstrappedField.getBootstrappedElement();

        if (!field.getType().equals(AraragiLogger.class)) {
            LOGGER.error(bootstrappedField.getDisplay() + " is not of type WinryLogger, skipping...");
            return;
        }

        if (bootstrappedField.getModifiers().contains(ReflectedModifier.FINAL)) {
            LOGGER.error(bootstrappedField.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
            return;
        }

        String name = annotation.value();

        if (name.equals(Logger.DEFAULT)) {
            name = bootstrappedField.getDeclaringClass().getDisplay();
        }

        AraragiLogger logger;

        if (annotation.level() != Logger.DEFAULT_LEVEL || !annotation.useDefault()) {
            logger = bootstrappedField.getBootstrapper().getLoggerFactory().createLogger(name, annotation.level());
        } else {
            logger = bootstrappedField.getBootstrapper().getLoggerFactory().createLogger(name);
        }

        bootstrappedField.getBootstrapper().getContext().register(logger);

        LOGGER.debug("Injecting: " + logger);
        bootstrappedField.getBootstrapper().inject(bootstrappedField, logger);
    }
}
