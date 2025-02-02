package institute.isshoni.winry.internal.annotation.processor.field;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.IAraragiLogger;
import institute.isshoni.araragi.logging.model.level.SimpleLevel;
import institute.isshoni.araragi.reflect.ReflectedModifier;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.annotation.logging.Logger;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedField;

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

        return makeLogger(annotation, parameter.getDeclaringExecutable().getDeclaringClass().getName()); // todo: use jstack to determine method name & use that.
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
        LogLevel level = annotation.level();

        if ((!level.name().equals(Logger.DEFAULT_LEVEL.getName()) || level.weight() != Logger.DEFAULT_LEVEL.getWeight())
                || !annotation.useDefault()) {
            logger = this.context.get().getLoggerFactory().createLogger(name, new SimpleLevel(level.name(), level.weight()));
        } else {
            logger = this.context.get().getLoggerFactory().createLogger(name);
        }

        this.context.get().registerToContext(logger);

        return logger;
    }
}
