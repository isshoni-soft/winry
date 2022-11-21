package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Depends;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.parameter.InjectProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Processor(InjectProcessor.class)
@Depends(Injected.class)
@Weight(200000)
public @interface Inject {

    String DEFAULT = "[DEFAULT]";

    String value() default DEFAULT;
}
