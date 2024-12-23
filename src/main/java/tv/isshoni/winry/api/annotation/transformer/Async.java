package tv.isshoni.winry.api.annotation.transformer;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.annotation.meta.BeforeInjections;
import tv.isshoni.winry.internal.annotation.processor.method.AsyncProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@BeforeInjections
@Processor(AsyncProcessor.class)
@Weight(100000000)
public @interface Async {
    boolean block() default false;
}
