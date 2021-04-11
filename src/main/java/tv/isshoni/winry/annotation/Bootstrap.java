package tv.isshoni.winry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bootstrap {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] value() default { };
}
