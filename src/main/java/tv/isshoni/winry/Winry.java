package tv.isshoni.winry;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.api.entity.context.IWinryContext;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Winry {

    public static IWinryContext bootstrap(Class<?> clazz, Object... provided) {
        AraragiLogger LOGGER;

        Instant start = Instant.now();

        Bootstrap bootstrap = clazz.getAnnotation(Bootstrap.class);

        if (bootstrap == null) {
            LOGGER = AraragiLogger.create("Winry");
            LOGGER.error(clazz.getName() + " does not have a @Bootstrap annotation, unable to properly bootstrap class!");
            return null;
        }

        LOGGER = AraragiLogger.create("Winry", bootstrap.defaultLevel());

        LOGGER.debug("Bootstrapping class " + clazz.getSimpleName() + " using bootstrapper " + bootstrap.bootstrapper().getSimpleName());

        IBootstrapper bootstrapper;
        try {
            LOGGER.debug("${dashes%50} Instantiating Bootstrapper ${dashes%50}");

            try {
                bootstrapper = bootstrap.bootstrapper().getConstructor(Bootstrap.class).newInstance(bootstrap);
                LOGGER.debug("Using @Bootstrap constructor...");
            } catch (NoSuchMethodException e) {
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

        LOGGER.debug("${dashes%50} Execution Complete (" + Duration.between(start, Instant.now()).toMillis() + " ms) ${dashes%50}");

        return bootstrapper.getContext();
    }
}
