package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.internal.annotation.processor.EventProcessor;
import institute.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Weight(Integer.MAX_VALUE - 5000)
@Processor({EventProcessor.class, BootstrapClassProcessor.class})
public @interface Event {

    String value() default "";

    boolean executable() default false;

    int weight() default -1;
}
