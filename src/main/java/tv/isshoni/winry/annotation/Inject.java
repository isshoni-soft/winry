package tv.isshoni.winry.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.InjectProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Processor(InjectProcessor.class)
@Weight(3)
public @interface Inject {

    String DEFAULT = "[DEFAULT]";

    String value() default DEFAULT;
}
