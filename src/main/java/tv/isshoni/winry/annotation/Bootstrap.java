package tv.isshoni.winry.annotation;

import tv.isshoni.winry.annotation.processor.BasicClassProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.bootstrap.SimpleBootstrapper;

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

    boolean injectable() default true;
}
