package tv.isshoni.winry.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class WinryLogFormatter extends Formatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    private int indent;

    public WinryLogFormatter() {
        this(0);
    }

    public WinryLogFormatter(int indent) {
        this.indent = indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder spaces = new StringBuilder();

        for (int x = 0; x < this.indent; x++) {
            spaces.append(' ');
        }

        return '[' + DATE_FORMATTER.format(Instant.now()) + "]: " + record.getLoggerName() + ' ' + record.getLevel().getLocalizedName() + " - " + spaces + record.getMessage() + '\n';
    }
}
