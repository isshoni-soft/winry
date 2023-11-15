package tv.isshoni.winry.api.context;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;

@Deprecated // todo: move me up into araragi-logger.
public interface ILoggerFactory {

    void setDefaultLoggerLevel(ILevel level);

    AraragiLogger createLogger(String name);

    AraragiLogger createLogger(Class<?> clazz);

    AraragiLogger createLogger(String name, ILevel level);

    ILevel getLevel();
}
