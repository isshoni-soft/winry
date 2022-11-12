package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.event.IEventBus;

public class WinryEventExecutable<T extends IEvent> implements IExecutable {

    private final int weight;

    private final Class<T> eventClass;

    private T event;

    private final IEventBus bus;

    public WinryEventExecutable(Class<T> eventClass, int weight, IEventBus bus) {
        this.eventClass = eventClass;
        this.weight = weight;
        this.bus = bus;
    }

    public WinryEventExecutable(T event, int weight, IEventBus bus) {
        this.event = event;
        this.weight = weight;
        this.bus = bus;
        this.eventClass = (Class<T>) event.getClass();
    }

    public T getEvent() {
        return this.event;
    }

    public Class<T> getEventClass() {
        return this.eventClass;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    public boolean hasEvent() {
        return this.getEvent() != null;
    }

    @Override
    public void execute() {
        if (this.hasEvent()) {
            this.bus.fire(this.getEvent());
        } else {
            this.bus.fire(this.getEventClass());
        }
    }

    @Override
    public String getDisplay() {
        return "Event: " + this.getEventClass().getName() + " (" + this.getWeight() + ")";
    }
}
