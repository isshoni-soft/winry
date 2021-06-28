package tv.isshoni.winry.annotation;

import tv.isshoni.winry.annotation.api.Processor;
import tv.isshoni.winry.annotation.api.Weight;
import tv.isshoni.winry.internal.annotation.processor.BasicClassProcessor;
import tv.isshoni.winry.entity.annotation.inject.InjectedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(value = Injected.DEFAULT_WEIGHT, dynamic = "weight")
@Processor(BasicClassProcessor.class)
public @interface Injected {

    int DEFAULT_WEIGHT = 5;

    InjectedType value() default InjectedType.DEFAULT;

    int weight() default DEFAULT_WEIGHT;
}
