package tv.isshoni.winry.annotation;

import tv.isshoni.winry.annotation.api.Processor;
import tv.isshoni.winry.annotation.api.Weight;
import tv.isshoni.winry.internal.annotation.processor.AsyncProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Processor(AsyncProcessor.class)
@Weight(3)
public @interface Async { }
