package model.annotation;

import model.annotation.processer.ReinjectProcessor;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Processor(ReinjectProcessor.class)
@Weight(200000)
public @interface Reinject {
}
