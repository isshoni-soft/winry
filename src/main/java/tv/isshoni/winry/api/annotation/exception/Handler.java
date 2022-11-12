package tv.isshoni.winry.api.annotation.exception;

import tv.isshoni.araragi.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(2005000000)
public @interface Handler {

    Class<? extends Throwable> value();

    boolean global() default false;

    boolean enforceSingleton() default false;
}
