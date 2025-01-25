package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.winry.api.annotation.meta.SingletonHolder;
import institute.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import institute.isshoni.winry.internal.model.bootstrap.IExecutableProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SingletonHolder
@Weight(Integer.MAX_VALUE - 10)
@Processor(BootstrapClassProcessor.class)
public @interface Loader {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    String[] excludePackage() default { };

    Class<?>[] manualLoad() default { };

    Class<?>[] manualExclude() default { };

    Class<? extends IExecutableProvider>[] providers() default { };
}
