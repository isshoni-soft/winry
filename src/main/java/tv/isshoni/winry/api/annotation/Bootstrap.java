package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.bootstrap.WinryEventProviders;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IExecutableProvider;
import tv.isshoni.winry.internal.annotation.processor.type.BasicClassProcessor;
import tv.isshoni.winry.api.bootstrap.SimpleBootstrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(Integer.MAX_VALUE)
@Processor(BasicClassProcessor.class)
public @interface Bootstrap {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    Class<?>[] manualLoad() default { };

    Class<? extends IExecutableProvider>[] providers() default { WinryEventProviders.class };

    Class<? extends IBootstrapper> bootstrapper() default SimpleBootstrapper.class;

    Level defaultLevel() default Level.ERROR;
}
