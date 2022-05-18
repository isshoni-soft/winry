package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.internal.annotation.processor.method.ListenerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Weight(1000000000)
@Processor(ListenerProcessor.class)
public @interface Listener {

    Class<? extends IEvent> value() default DummyEvent.class;

    int weight() default 100;

    boolean ignoreCancelled() default false;

    interface DummyEvent extends IEvent { }
}