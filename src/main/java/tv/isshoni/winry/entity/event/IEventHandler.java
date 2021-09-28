package tv.isshoni.winry.entity.event;

import java.lang.reflect.Method;

public interface IEventHandler {

    Method getHandler();

    Object getTarget();

    boolean isStatic();
}
