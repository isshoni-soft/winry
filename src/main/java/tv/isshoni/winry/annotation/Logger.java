package tv.isshoni.winry.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.internal.annotation.processor.field.LoggerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Processor(LoggerProcessor.class)
@Weight(7)
public @interface Logger {

    String DEFAULT = "[DEFAULT]";

    Level DEFAULT_LEVEL = Level.ERROR;

    String value() default DEFAULT;

    Level level() default Level.ERROR;

    boolean useDefault() default true;
}
