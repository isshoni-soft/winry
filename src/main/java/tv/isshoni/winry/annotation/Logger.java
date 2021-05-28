package tv.isshoni.winry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Weight(7)
public @interface Logger {

    String value() default "Logger";

    int indent() default 0;
}
