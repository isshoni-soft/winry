package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.internal.annotation.processor.parameter.InjectProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Processor(InjectProcessor.class)
@Weight(200000)
public @interface Inject {

    String DEFAULT = "[DEFAULT]";

    String value() default DEFAULT;
}
