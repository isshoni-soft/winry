package tv.isshoni.winry.api.annotation.exception;

import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.exception.IExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Weight(2000000000)
public @interface ExceptionHandler {

    Class<? extends IExceptionHandler<?>> value();

    boolean useSingleton() default false;
}
