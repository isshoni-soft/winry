package tv.isshoni.winry.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class WinryLogger extends Logger {

    private static final WinryLogFormatter FORMATTER = new WinryLogFormatter();

    public static WinryLogger create(String name) {
        return new WinryLogger(name);
    }

    protected WinryLogger(String name) {
        super(name, "sun.util.logging.resources.logging");

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(FORMATTER);

        addHandler(handler);
    }
}
