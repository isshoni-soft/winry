package tv.isshoni.winry.annotation;

import tv.isshoni.winry.annotation.processor.BasicMethodProcessor;
import tv.isshoni.winry.entity.annotation.runner.RunnerOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Processor(BasicMethodProcessor.class)
@Weight(value = Runner.DEFAULT_WEIGHT, dynamic = "weight")
public @interface Runner {

    int DEFAULT_WEIGHT = 2;

    RunnerOrder value() default RunnerOrder.INIT;

    int weight() default DEFAULT_WEIGHT;
}
