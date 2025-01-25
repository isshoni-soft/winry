package institute.isshoni.winry.api.annotation.transformer;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.api.annotation.meta.BeforeInjections;
import institute.isshoni.winry.internal.annotation.processor.method.AsyncProcessor;

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
