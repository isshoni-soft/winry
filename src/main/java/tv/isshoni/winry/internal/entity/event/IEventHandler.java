package tv.isshoni.winry.internal.entity.event;

import tv.isshoni.winry.api.event.IEvent;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;

public interface IEventHandler extends Comparable<IEventHandler> {

    void execute(IEvent event);

    BootstrappedMethod getHandler();

    Class<? extends IEvent> getTargetEvent();

    int getWeight();

    boolean shouldIgnoreCancelled();

    boolean needsMainThread();

    default int compareTo(IEventHandler o) {
        int weight = Integer.compare(this.getWeight(), o.getWeight());

        if (!this.getTargetEvent().equals(o.getTargetEvent())) {
            weight -= 1000;
        }

        return weight;
    }
}
