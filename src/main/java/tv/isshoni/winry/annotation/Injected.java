package tv.isshoni.winry.annotation;

import tv.isshoni.winry.entity.inject.InjectedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Injected {

    int DEFAULT_WEIGHT = 5;

    InjectedType value() default InjectedType.DEFAULT;

    int weight() default DEFAULT_WEIGHT;
}
