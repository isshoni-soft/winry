package tv.isshoni.winry.internal.entity.event;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;

public interface IEventHandler extends Comparable<IEventHandler> {

    void execute(Object event);

    BootstrappedMethod getHandler();

    Class<?> getTargetEvent();

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
