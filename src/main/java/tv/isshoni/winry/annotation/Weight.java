package tv.isshoni.winry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Weight {

    String NOT_DYNAMIC = "111111";

    int value();

    String weightEnum() default "value";
    String dynamic() default NOT_DYNAMIC;
}
