package tv.isshoni.winry.internal.annotation.processor.field;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedField;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public class LoggerProcessor implements IWinryAdvancedAnnotationProcessor<Logger, AraragiLogger> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public LoggerProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        LOGGER = context.getLoggerFactory().createLogger("LoggerProcessor");
    }

    @Override
    public AraragiLogger supply(Logger annotation, AraragiLogger previous, Parameter parameter) {
        if (!IAraragiLogger.class.isAssignableFrom(parameter.getType())) {
            LOGGER.error(parameter.getType().getName() + " is not assignable to AraragiLogger.");
            return null;
        }

        return makeLogger(annotation, parameter.getName()); // todo: use jstack to determine method name & use that.
    }

    @Override
    public void executeField(IAnnotatedField meta, Object target, Logger annotation) {
        Field field = meta.getElement();

        if (!IAraragiLogger.class.isAssignableFrom(field.getType())) {
            LOGGER.error(meta.getDisplay() + " is not assignable to AraragiLogger, skipping...");
            return;
        }

        if (meta.getModifiers().contains(ReflectedModifier.FINAL)) {
            LOGGER.error(meta.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
            return;
        }

        AraragiLogger logger = makeLogger(annotation, meta.getDeclaringClass().getElement().getSimpleName());

        LOGGER.debug("Injecting logger: " + logger + " into " + target.getClass());
        this.context.get().getMetaManager().inject(meta, target, logger);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }

    private AraragiLogger makeLogger(Logger annotation, String otherwise) {
        String name = annotation.value();

        if (name.equals(Logger.DEFAULT)) {
            name = otherwise;
        }

        AraragiLogger logger;

        if (annotation.level() != Logger.DEFAULT_LEVEL || !annotation.useDefault()) {
            logger = this.context.get().getLoggerFactory().createLogger(name, annotation.level());
        } else {
            logger = this.context.get().getLoggerFactory().createLogger(name);
        }

        this.context.get().registerToContext(logger);

        return logger;
    }
}
