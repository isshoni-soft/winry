package tv.isshoni.winry.annotation.api;

import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Processor {

    Class<? extends IWinryAnnotationProcessor<?>>[] value();
}
