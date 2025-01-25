package tv.isshoni.winry.api.annotation.transformer;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.annotation.meta.BeforeInjections;
import tv.isshoni.winry.internal.annotation.processor.method.ProfileProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@BeforeInjections
@Processor(ProfileProcessor.class)
@Weight(10000000)
public @interface Profile { }
