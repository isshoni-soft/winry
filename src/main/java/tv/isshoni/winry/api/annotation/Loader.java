package tv.isshoni.winry.api.annotation;

import tv.isshoni.winry.api.bootstrap.WinryEventProviders;
import tv.isshoni.winry.entity.bootstrap.IExecutableProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Loader {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    Class<?>[] manualLoad() default { };

    Class<? extends IExecutableProvider>[] providers() default { WinryEventProviders.class };
}
