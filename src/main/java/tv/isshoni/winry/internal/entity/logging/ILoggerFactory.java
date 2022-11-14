package tv.isshoni.winry.internal.entity.logging;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;

public interface ILoggerFactory {

    void setDefaultLoggerLevel(ILevel level);

    AraragiLogger createLogger(String name);

    AraragiLogger createLogger(Class<?> clazz);

    AraragiLogger createLogger(String name, ILevel level);

    ILevel getLevel();
}
