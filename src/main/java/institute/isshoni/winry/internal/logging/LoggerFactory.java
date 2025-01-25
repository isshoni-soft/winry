package institute.isshoni.winry.internal.logging;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.logging.model.level.ILevel;
import institute.isshoni.araragi.logging.model.level.Level;

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
