package tv.isshoni.winry.api.annotation;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.Weight;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.bootstrap.WinryBootstrapper;
import tv.isshoni.winry.internal.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Weight(Integer.MAX_VALUE)
@Processor(BootstrapClassProcessor.class)
public @interface Bootstrap {

    String name() default "DEFAULT-BOOTSTRAP-NAME";

    Loader loader() default @Loader;

    Class<? extends IBootstrapper> bootstrapper() default WinryBootstrapper.class;

    Level defaultLevel() default Level.ERROR;

    boolean disableDefaultProvider() default false;

    boolean disableDefaultPackage() default false;

    boolean disableForkMain() default false;
}
