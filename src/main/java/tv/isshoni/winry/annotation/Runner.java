package tv.isshoni.winry.annotation;

import tv.isshoni.winry.entity.runner.RunnerOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Runner {

    int DEFAULT_WEIGHT = 2;

    RunnerOrder value() default RunnerOrder.INIT;

    int weight() default DEFAULT_WEIGHT;
}