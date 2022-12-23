package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.winry.internal.model.bootstrap.IExecutableProvider;
import tv.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(Integer.MAX_VALUE - 10)
@Processor(BootstrapClassProcessor.class)
public @interface Loader {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    Class<?>[] manualLoad() default { };

    Class<? extends IExecutableProvider>[] providers() default { };
}
