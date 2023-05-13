package tv.isshoni.winry.internal.annotation.processor.field;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedField;

import java.lang.reflect.Field;

// Cool idea, use stackwalker to auto-build loggers for parameter injected loggers.
// Also allow empty string field-level annotations to detect from class & use class name (original class name)
public class LoggerProcessor implements IWinryAnnotationProcessor<Logger> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public LoggerProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
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
            logger = this.context.get().getLoggerFactory().createLogger(name, annotation.level());
        } else {
            logger = this.context.get().getLoggerFactory().createLogger(name);
        }

        this.context.get().registerToContext(logger);

        LOGGER.debug("Injecting logger: " + logger + " into " + target.getClass());
        this.context.get().getMetaManager().inject(meta, target, logger);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
