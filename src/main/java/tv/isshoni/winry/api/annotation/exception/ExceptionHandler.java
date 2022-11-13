package tv.isshoni.winry.api.annotation.exception;

import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.exception.IExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Weight(2000000000)
// TODO: Make me repeatable -- implement me when araragi meta-annotations are done.
public @interface ExceptionHandler {

    Class<? extends IExceptionHandler<?>> value();

    boolean useSingleton() default false;
}
