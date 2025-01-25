package institute.isshoni.winry.internal.model.event;

import java.util.UUID;

public interface IEventHandler<T> extends Comparable<IEventHandler<T>> {

    UUID getId();

    void execute(T event);

    Class<? extends T> getTargetEvent();

    int getWeight();

    boolean shouldIgnoreCancelled();

    boolean requiresExact();

    default int compareTo(IEventHandler o) {
        int weight = Integer.compare(this.getWeight(), o.getWeight());

        if (!this.getTargetEvent().equals(o.getTargetEvent())) {
            weight += 1000;
        }

        return weight;
    }
}
