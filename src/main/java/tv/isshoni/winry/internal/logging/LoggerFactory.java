package tv.isshoni.winry.internal.logging;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.context.ILoggerFactory;

public class LoggerFactory implements ILoggerFactory {

    private ILevel level;

    public LoggerFactory() {
        this.level = Level.ERROR;
    }

    @Override
    public void setDefaultLoggerLevel(ILevel level) {
        this.level = level;
    }

    @Override
    public AraragiLogger createLogger(String name) {
        AraragiLogger result = AraragiLogger.create(name);
        result.setLevel(this.level);

        return result;
    }

    @Override
    public AraragiLogger createLogger(Class<?> clazz) {
        return createLogger(clazz.getSimpleName());
    }

    @Override
    public AraragiLogger createLogger(String name, ILevel level) {
        return AraragiLogger.create(name, level);
    }

    @Override
    public ILevel getLevel() {
        return this.level;
    }
}
