package tv.isshoni.winry.api.annotation.transformer;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.internal.annotation.processor.method.OnMainProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transformer
@Processor(OnMainProcessor.class)
@Weight(100000000)
public @interface OnMain {
    boolean block() default false;
}
