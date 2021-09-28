package tv.isshoni.winry.api.entity.event;

import java.lang.reflect.Method;

public interface IEventHandler extends Comparable<IEventHandler> {

    Method getHandler();

    Object getTarget();

    int getWeight();

    boolean isStatic();

    default int compareTo(IEventHandler o) {
        return Integer.compare(this.getWeight(), o.getWeight());
    }
}
