package tv.isshoni.winry.annotation;

import tv.isshoni.winry.bootstrap.ApplicationBootstrapper;
import tv.isshoni.winry.bootstrap.IBootstrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bootstrap {

    /**
     * @return Array of paths to check for related bootstrapper annotations
     */
    String[] loadPackage() default { };

    Class<?>[] manualLoad() default { };

    Class<? extends IBootstrapper> bootstrapper() default ApplicationBootstrapper.class;

    boolean injectable() default true;
}
