package tv.isshoni.winry.api;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IBootstrapContext;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.BootstrapContext;
import tv.isshoni.winry.internal.async.WinryAsyncManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Winry {

    public static IWinryContext bootstrap(Class<?> clazz) throws ExecutionException, InterruptedException {
        return bootstrap(clazz, new Object[0]);
    }

    public static IWinryContext bootstrap(Class<?> clazz, String[] args) throws ExecutionException, InterruptedException {
        return bootstrap(clazz, args, new Object[0]);
    }

    public static IWinryContext bootstrap(Class<?> clazz, Object[] provided) throws ExecutionException, InterruptedException {
        return bootstrap(clazz, new String[0], provided);
    }

    public static IWinryContext bootstrap(Class<?> clazz, String[] arguments, Object[] provided) throws ExecutionException, InterruptedException {
        Instant start = Instant.now();

        Bootstrap bootstrap = clazz.getAnnotation(Bootstrap.class);

        if (bootstrap == null) {
            AraragiLogger LOGGER = AraragiLogger.create("Winry");
            LOGGER.error(clazz.getName() + " does not have a @Bootstrap annotation, unable to properly bootstrap class!");
            return null;
        }

        ILoggerFactory loggerFactory = ReflectionUtil.construct(bootstrap.loggerFactory());
        loggerFactory.setDefaultLoggerLevel(bootstrap.defaultLevel());
        IWinryAsyncManager asyncManager = new WinryAsyncManager(bootstrap, loggerFactory);
        IBootstrapContext bootstrapContext = BootstrapContext.builder()
                .arguments(arguments)
                .asyncManager(asyncManager)
                .loggerFactory(loggerFactory)
                .forked(!bootstrap.noFork()).build();

        if (bootstrap.noFork()) {
            return bootstrapInThread(clazz, bootstrapContext, start, bootstrap, provided);
        }

        return asyncManager.forkMain(() -> bootstrapInThread(clazz, bootstrapContext, start, bootstrap, provided));
    }

    public static IWinryContext bootstrapInThread(Class<?> clazz, IBootstrapContext bootstrapContext, Instant start,
                                                  Bootstrap bootstrap, Object... provided) {
        AraragiLogger LOGGER = bootstrapContext.getLoggerFactory().createLogger("Winry");

        LOGGER.info("Bootstrapping class ${0} using bootstrapper ${1}", clazz.getSimpleName(), bootstrap.bootstrapper().getSimpleName());

        IBootstrapper bootstrapper;
        try {
            LOGGER.debug("${a:dashes%50} Instantiating Bootstrapper ${a:dashes%50}");
            Class<?> bootstrapperClass = bootstrap.bootstrapper();

            if (ReflectionUtil.hasConstructor(bootstrapperClass, Bootstrap.class, IBootstrapContext.class)) {
                LOGGER.debug("Constructing new bootstrapper instance ...");
                bootstrapper = bootstrap.bootstrapper().getConstructor(Bootstrap.class, IBootstrapContext.class)
                        .newInstance(bootstrap, bootstrapContext);
            } else {
                throw new IllegalStateException("Bootstrapper constructors must accept IBootstrapContext!");
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("Unable to instantiate new instance of bootstrapper class: ${0}", bootstrap.bootstrapper().getName());
            e.printStackTrace();
            return null;
        }

        LOGGER.debug("Handing off to bootstrapper...");
        bootstrapper.bootstrap(bootstrap, clazz, Stream.of(provided).collect(Collectors.toMap(Object::getClass, o -> o)));

        LOGGER.info("${a:dashes%50} Execution Complete (${0} ms) ${a:dashes%50}", Duration.between(start, Instant.now()).toMillis());

        return bootstrapper.getContext();
    }
}
