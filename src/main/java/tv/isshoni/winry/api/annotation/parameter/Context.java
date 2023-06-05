package tv.isshoni.winry.api.annotation.parameter;

import tv.isshoni.araragi.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Weight(400000)
public @interface Context { }
