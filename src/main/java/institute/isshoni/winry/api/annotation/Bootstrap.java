package institute.isshoni.winry.api.annotation;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.winry.api.Winry;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.annotation.meta.SingletonHolder;
import institute.isshoni.winry.api.bootstrap.WinryBootstrapper;
import institute.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import institute.isshoni.winry.internal.logging.LoggerFactory;
import institute.isshoni.winry.internal.model.bootstrap.IBootstrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SingletonHolder
@Weight(Integer.MAX_VALUE)
@Processor(BootstrapClassProcessor.class)
public @interface Bootstrap {

    String name() default "DEFAULT-BOOTSTRAP-NAME";

    Loader loader() default @Loader;

    Class<? extends IBootstrapper> bootstrapper() default WinryBootstrapper.class;

    // todo: come up with a good quickhand way to configure this; it shouldn't require a specific logger factory everytime.
    LogLevel defaultLevel() default @LogLevel(name = Winry.STDOUT_NAME, weight = Winry.STDOUT_WEIGHT);

    Class<? extends ILoggerFactory> loggerFactory() default LoggerFactory.class;

    boolean disableDefaultProvider() default false;

    boolean disableDefaultPackage() default false;

    boolean noFork() default false;
}
