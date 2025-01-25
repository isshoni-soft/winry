package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.IncompatibleWith;
import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.api.annotation.transformer.Async;
import institute.isshoni.winry.api.annotation.transformer.OnMain;
import institute.isshoni.winry.internal.annotation.processor.method.ListenerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Weight(1000000000)
@Processor(ListenerProcessor.class)
@IncompatibleWith({OnMain.class, Async.class})
public @interface Listener {

    Class<?> value();

    int weight() default 100;

    boolean ignoreCancelled() default false;

    boolean requireExact() default false;
}
