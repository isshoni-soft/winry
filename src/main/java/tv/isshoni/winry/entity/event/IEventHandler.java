package tv.isshoni.winry.entity.event;

import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.reflect.Method;

public interface IEventHandler extends Comparable<IEventHandler> {

    void execute(IEvent event);

    BootstrappedMethod getHandler();

    Class<? extends IEvent> getTargetEvent();

    int getWeight();

    boolean shouldIgnoreCancelled();

    default int compareTo(IEventHandler o) {
        int weight = Integer.compare(this.getWeight(), o.getWeight());

        if (!this.getTargetEvent().equals(o.getTargetEvent())) {
            weight -= 1000;
        }

        return weight;
    }
}
