package tv.isshoni.winry.annotation;

import tv.isshoni.winry.annotation.api.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Weight(4) // TODO: Determine if this is a correct weight.
public @interface ExceptionHandler {

    Class<? extends Throwable> value();
}
