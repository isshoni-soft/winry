package tv.isshoni.winry;

import tv.isshoni.winry.logging.WinryLogger;

public class Winry {

    private static final WinryLogger LOGGER = WinryLogger.create("Winry");

    public static void bootstrap(Class<?> clazz, Object... provided) {
        LOGGER.info("Bootstrapping class: " + clazz.getName());
    }
}
