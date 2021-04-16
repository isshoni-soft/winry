package tv.isshoni.winry.annotation;

import tv.isshoni.winry.entity.inject.InjectedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Injected {

    InjectedType value() default InjectedType.DEFAULT;
}
