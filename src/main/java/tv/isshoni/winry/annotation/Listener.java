package tv.isshoni.winry.annotation;

import tv.isshoni.araragi.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Weight(2)
public @interface Listener {

    /**
     * This represents
     *
     * @return
     */
    boolean value() default true;
}
