package tv.isshoni.winry.annotation;

import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.annotation.api.Processor;
import tv.isshoni.winry.annotation.api.Weight;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.annotation.processor.BasicClassProcessor;
import tv.isshoni.winry.internal.bootstrap.SimpleBootstrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(Integer.MAX_VALUE - 50000)
@Processor(BasicClassProcessor.class)
public @interface Bootstrap {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    Class<?>[] manualLoad() default { };

    Class<? extends IBootstrapper> bootstrapper() default SimpleBootstrapper.class;

    Level defaultLevel() default Level.ERROR;
}
