package institute.isshoni.winry.api.annotation.exception;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.api.annotation.meta.BeforeInjections;
import institute.isshoni.winry.api.exception.IExceptionHandler;
import institute.isshoni.winry.internal.annotation.processor.method.ExceptionHandlerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@BeforeInjections
@Processor(ExceptionHandlerProcessor.class)
@Weight(2000000000)
// TODO: Make me repeatable -- implement me when araragi meta-annotations are done.
public @interface ExceptionHandler {

    Class<? extends IExceptionHandler<?>> value();

    boolean useSingleton() default false;
}
