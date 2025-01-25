package tv.isshoni.winry.api.annotation.parameter;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.parameter.NewProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Weight(4)
@Processor(NewProcessor.class)
public @interface New { }
