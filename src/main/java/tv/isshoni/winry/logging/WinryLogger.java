package tv.isshoni.winry.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

// TODO: Write a custom logger that isn't based on the Java logger, because it's not all that good.
public class WinryLogger extends Logger {

    public static WinryLogger create(String name) {
        return create(name, 0);
    }

    public static WinryLogger create(String name, Integer indent) {
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

    @Override
    public String toString() {
        return "WinryLogger[name=" + this.getName() + ",indent=" + this.formatter.getIndent() + "]";
    }
}
