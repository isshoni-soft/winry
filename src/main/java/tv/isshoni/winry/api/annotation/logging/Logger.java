package tv.isshoni.winry.api.annotation.logging;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.winry.api.Winry;
import tv.isshoni.winry.internal.annotation.processor.field.LoggerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Processor(LoggerProcessor.class)
@Weight(1200000)
public @interface Logger {

    String DEFAULT = "[DEFAULT]";

    ILevel DEFAULT_LEVEL = Winry.STDOUT;

    String value() default DEFAULT;

    LogLevel level() default @LogLevel(name = Winry.STDOUT_NAME, weight = Winry.STDOUT_WEIGHT);

    boolean useDefault() default true;
}
