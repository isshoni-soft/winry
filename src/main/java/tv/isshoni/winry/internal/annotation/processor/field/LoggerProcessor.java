package tv.isshoni.winry.internal.annotation.processor.field;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;

import java.lang.reflect.Field;

public class LoggerProcessor implements IWinryAnnotationProcessor<Logger> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public LoggerProcessor(@Context IWinryContext context) {
        this.context = context;
        LOGGER = context.getLoggerFactory().createLogger("LoggerProcessor");
    }

    @Override
    public void executeField(IAnnotatedField meta, Object target, Logger annotation) {
        Field field = meta.getElement();

        if (!field.getType().isAssignableFrom(AraragiLogger.class)) {
            LOGGER.error(meta.getDisplay() + " is not assignable to AraragiLogger, skipping...");
            return;
        }

        if (meta.getModifiers().contains(ReflectedModifier.FINAL)) {
            LOGGER.error(meta.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
            return;
        }

        String name = annotation.value();

        if (name.equals(Logger.DEFAULT)) {
            name = meta.getDeclaringClass().getDisplay();
        }

        AraragiLogger logger;

        if (annotation.level() != Logger.DEFAULT_LEVEL || !annotation.useDefault()) {
            logger = this.context.getLoggerFactory().createLogger(name, annotation.level());
        } else {
            logger = this.context.getLoggerFactory().createLogger(name);
        }

        this.context.registerToContext(logger);

        LOGGER.debug("Injecting: " + logger);
        this.context.getMetaManager().inject(meta, target, logger);
    }

    @Override
    public void executeField(BootstrappedField bootstrappedField, Logger annotation) {
        Field field = bootstrappedField.getBootstrappedElement();

        if (!field.getType().isAssignableFrom(AraragiLogger.class)) {
            LOGGER.error(bootstrappedField.getDisplay() + " is not assignable to AraragiLogger, skipping...");
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
            logger = bootstrappedField.getBootstrapper().getContext().getLoggerFactory().createLogger(name, annotation.level());
        } else {
            logger = bootstrappedField.getBootstrapper().getContext().getLoggerFactory().createLogger(name);
        }

        bootstrappedField.getBootstrapper().getContext().registerToContext(logger);

        LOGGER.debug("Injecting: " + logger);
        bootstrappedField.getBootstrapper().getContext().getElementBootstrapper().inject(bootstrappedField, logger);
    }
}
