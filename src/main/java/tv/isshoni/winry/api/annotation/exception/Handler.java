package tv.isshoni.winry.api.annotation.exception;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.annotation.meta.SingletonHolder;
import tv.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import tv.isshoni.winry.internal.annotation.processor.type.HandlerProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SingletonHolder
@Processor({HandlerProcessor.class, BootstrapClassProcessor.class})
@Weight(2005000000)
public @interface Handler {

    Class<? extends Throwable> value();

    boolean global() default false;

    boolean enforceSingleton() default false;
}
