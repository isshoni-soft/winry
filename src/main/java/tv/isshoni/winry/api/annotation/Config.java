package tv.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Depends;
import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.api.annotation.meta.SingletonHolder;
import tv.isshoni.winry.api.config.IConfigSerializer;
import tv.isshoni.winry.api.config.JSONSerializer;
import tv.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import tv.isshoni.winry.internal.annotation.processor.type.ConfigClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SingletonHolder
@Weight(value = Config.DEFAULT_WEIGHT, dynamic = "weight")
@Depends(Inject.class)
@Processor({BootstrapClassProcessor.class, ConfigClassProcessor.class})
public @interface Config {

    int DEFAULT_WEIGHT = Integer.MAX_VALUE - 500;

    String value();

    Class<? extends IConfigSerializer<?>> serializer() default JSONSerializer.class;

    int weight() default DEFAULT_WEIGHT;

    boolean internal() default false;
}
