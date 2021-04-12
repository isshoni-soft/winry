package tv.isshoni.winry;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.bootstrap.IBootstrapper;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.InvocationTargetException;

public class Winry {

    private static final WinryLogger LOGGER = WinryLogger.create("Winry");

    public static void bootstrap(Class<?> clazz, Object... provided) {
        Bootstrap bootstrap = clazz.getAnnotation(Bootstrap.class);

        if (bootstrap == null) {
            LOGGER.severe(clazz.getName() + " does not have a @Bootstrap annotation, unable to properly bootstrap class!");
            return;
        }

        LOGGER.info("Bootstrapping class " + clazz.getSimpleName() + " using bootstrapper " + bootstrap.bootstrapper().getSimpleName());

        IBootstrapper bootstrapper;
        try {
            bootstrapper = bootstrap.bootstrapper().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.severe("Unable to instantiate new instance of bootstrapper class: " + bootstrap.bootstrapper().getName());
            e.printStackTrace();
            return;
        }

        LOGGER.info("Running bootstrapper...");
        bootstrapper.bootstrap(bootstrap, clazz, provided);
    }
}
