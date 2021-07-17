package tv.isshoni.winry;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Winry {

    private static final WinryLogger LOGGER = WinryLogger.create("Winry");

    private static Bootstrap bootstrap;

    private static IBootstrapper bootstrapper;

    public static void bootstrap(Class<?> clazz, Object... provided) {
        Instant start = Instant.now();

        bootstrap = clazz.getAnnotation(Bootstrap.class);

        if (bootstrap == null) {
            LOGGER.severe(clazz.getName() + " does not have a @Bootstrap annotation, unable to properly bootstrap class!");
            return;
        }

        LOGGER.info("Bootstrapping class " + clazz.getSimpleName() + " using bootstrapper " + bootstrap.bootstrapper().getSimpleName());

        try {
            LOGGER.info("Instantiating bootstrapper...");
            bootstrapper = bootstrap.bootstrapper().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.severe("Unable to instantiate new instance of bootstrapper class: " + bootstrap.bootstrapper().getName());
            e.printStackTrace();
            return;
        }

        LOGGER.info("Handing off to bootstrapper...");
        bootstrapper.bootstrap(bootstrap, clazz, Stream.of(provided).collect(Collectors.toMap(Object::getClass, o -> o)));

        LOGGER.info("Finished in " + Duration.between(start, Instant.now()).toMillis() + " ms");
    }

    public static IBootstrapper getBootstrapper() {
        return bootstrapper;
    }

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}
