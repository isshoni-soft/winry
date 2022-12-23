package tv.isshoni.winry.api;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.async.WinryAsyncManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Winry {

    public static IWinryContext bootstrap(Class<?> clazz, Object... provided) throws ExecutionException, InterruptedException {
        Instant start = Instant.now();

        Bootstrap bootstrap = clazz.getAnnotation(Bootstrap.class);

        if (bootstrap == null) {
            AraragiLogger LOGGER = AraragiLogger.create("Winry");
            LOGGER.error(clazz.getName() + " does not have a @Bootstrap annotation, unable to properly bootstrap class!");
            return null;
        }

        IWinryAsyncManager asyncManager = new WinryAsyncManager(bootstrap.name());

        if (bootstrap.disableForkMain()) {
            return bootstrapInThread(clazz, asyncManager, start, bootstrap, provided);
        }

        return asyncManager.forkMain(() -> bootstrapInThread(clazz, asyncManager, start, bootstrap, provided));
    }

    public static IWinryContext bootstrapInThread(Class<?> clazz, IWinryAsyncManager asyncManager, Instant start, Bootstrap bootstrap, Object... provided) {
        AraragiLogger LOGGER = AraragiLogger.create("Winry", bootstrap.defaultLevel());

        LOGGER.info("Bootstrapping class " + clazz.getSimpleName() + " using bootstrapper " + bootstrap.bootstrapper().getSimpleName());

        IBootstrapper bootstrapper;
        try {
            LOGGER.debug("${dashes%50} Instantiating Bootstrapper ${dashes%50}");
            Class<?> bootstrapperClass = bootstrap.bootstrapper();

            if (ReflectionUtil.hasConstructor(bootstrapperClass, Bootstrap.class, IWinryAsyncManager.class)) {
                bootstrapper = bootstrap.bootstrapper().getConstructor(Bootstrap.class, IWinryAsyncManager.class)
                        .newInstance(bootstrap, asyncManager);
                LOGGER.debug("Using @Bootstrap, IWinryAsyncManager constructor...");
            } else if (ReflectionUtil.hasConstructor(bootstrapperClass, Bootstrap.class)) {
                bootstrapper = bootstrap.bootstrapper().getConstructor(Bootstrap.class)
                        .newInstance(bootstrap);
                LOGGER.debug("Using @Bootstrap constructor...");
            } else {
                bootstrapper = bootstrap.bootstrapper().getConstructor().newInstance();
                LOGGER.debug("Using default constructor...");
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("Unable to instantiate new instance of bootstrapper class: " + bootstrap.bootstrapper().getName());
            e.printStackTrace();
            return null;
        }

        LOGGER.debug("Handing off to bootstrapper...");
        bootstrapper.bootstrap(bootstrap, clazz, Stream.of(provided).collect(Collectors.toMap(Object::getClass, o -> o)));

        LOGGER.info("${dashes%50} Execution Complete (" + Duration.between(start, Instant.now()).toMillis() + " ms) ${dashes%50}");

        return bootstrapper.getContext();
    }
}
