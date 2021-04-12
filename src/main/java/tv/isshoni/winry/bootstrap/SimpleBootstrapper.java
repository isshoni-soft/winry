package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.logging.WinryLogger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SimpleBootstrapper implements IBootstrapper {

    private static final WinryLogger LOGGER = WinryLogger.create("SimpleBootstrapper");

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> baseClazz, Object... provided) {
        LOGGER.info("Beginning class discovery process...");

        List<Class<?>> clazzes = new LinkedList<>();
        clazzes.add(baseClazz);
        clazzes.addAll(Arrays.asList(bootstrap.manualLoad()));

        LOGGER.info("Discovered manually loaded classes: " + Arrays.toString(bootstrap.manualLoad()));

        // TODO: Load package class
    }
}
