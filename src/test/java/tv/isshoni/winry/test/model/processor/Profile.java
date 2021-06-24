package tv.isshoni.winry.test.model.processor;

import tv.isshoni.winry.annotation.Processor;
import tv.isshoni.winry.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Processor(ProfileProcessor.class)
@Weight(3)
public @interface Profile { }
