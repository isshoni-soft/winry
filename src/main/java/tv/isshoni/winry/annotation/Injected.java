package tv.isshoni.winry.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.type.BasicClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(value = Injected.DEFAULT_WEIGHT, dynamic = "value")
@Processor(BasicClassProcessor.class)
public @interface Injected {

    int DEFAULT_WEIGHT = Integer.MAX_VALUE - 500;

    int value() default DEFAULT_WEIGHT;
}
