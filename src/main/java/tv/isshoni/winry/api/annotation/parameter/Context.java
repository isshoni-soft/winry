package tv.isshoni.winry.api.annotation.parameter;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.annotation.processor.parameter.WinryContextProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Weight(5)
@Processor(WinryContextProcessor.class)
public @interface Context { }
