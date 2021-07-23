package tv.isshoni.winry.internal.logging;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

public class LoggerFactory implements ILoggerFactory {

    @Override
    public void setDefaultLoggerLevel(ILevel level) {

    }

    @Override
    public AraragiLogger createDefaultLogger(String name) {
        return null;
    }

    @Override
    public AraragiLogger createLogger(String name, ILevel level) {
        return null;
    }
}
