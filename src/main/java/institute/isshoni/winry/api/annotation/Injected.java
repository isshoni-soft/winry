package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Depends;
import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.api.annotation.meta.SingletonHolder;
import institute.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SingletonHolder
@Weight(value = Injected.DEFAULT_WEIGHT, dynamic = "value")
@Depends(Inject.class)
@Processor(BootstrapClassProcessor.class)
public @interface Injected {

    int DEFAULT_WEIGHT = Integer.MAX_VALUE - 500;

    int value() default DEFAULT_WEIGHT;
}
