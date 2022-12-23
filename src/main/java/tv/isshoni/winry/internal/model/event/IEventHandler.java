package tv.isshoni.winry.internal.model.event;

public interface IEventHandler<T> extends Comparable<IEventHandler<T>> {

    void execute(T event);

    Class<? extends T> getTargetEvent();

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
