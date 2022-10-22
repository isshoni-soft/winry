package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.type.ExecutableEventProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(Integer.MAX_VALUE - 5000)
@Processor(ExecutableEventProcessor.class)
public @interface ExecutableEvent {
    int value();
}
