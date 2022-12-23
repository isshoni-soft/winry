package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.winry.internal.model.event.IEventBus;

public class WinryEventExecutable implements IExecutable {

    private final int weight;

    private final Class<?> eventClass;

    private Object event;

    private final IEventBus bus;

    public WinryEventExecutable(Class<?> eventClass, int weight, IEventBus bus) {
        this.eventClass = eventClass;
        this.weight = weight;
        this.bus = bus;
    }

    public WinryEventExecutable(Object event, int weight, IEventBus bus) {
        this.event = event;
        this.weight = weight;
        this.bus = bus;
        this.eventClass = event.getClass();
    }

    public Object getEvent() {
        return this.event;
    }

    public Class<?> getEventClass() {
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WinryEventExecutable other)) {
            return false;
        }

        return other.bus.equals(this.bus) && other.eventClass.equals(this.eventClass) && other.weight == this.weight;
    }
}
