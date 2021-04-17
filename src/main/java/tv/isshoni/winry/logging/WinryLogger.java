package tv.isshoni.winry.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class WinryLogger extends Logger {

    public static WinryLogger create(String name) {
        return create(name, 0);
    }

    public static WinryLogger create(String name, int indent) {
        return new WinryLogger(name, indent);
    }

    private final WinryLogFormatter formatter;

    protected WinryLogger(String name, int indent) {
        super(name, "sun.util.logging.resources.logging");

        this.formatter = new WinryLogFormatter(indent);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(this.formatter);

        addHandler(handler);
    }

    public void setIndent(int indent) {
        this.formatter.setIndent(indent);
    }
}
