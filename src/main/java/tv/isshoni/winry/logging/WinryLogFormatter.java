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

    @Override
    public synchronized String format(LogRecord record) {
        return '[' + DATE_FORMATTER.format(Instant.now()) + "]: " + record.getLoggerName() + ' ' + record.getLevel().getLocalizedName() + " - " + record.getMessage() + '\n';
    }
}
