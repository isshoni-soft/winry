package institute.isshoni.winry.api.bootstrap.executable;

import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.exception.EventExecutionException;

public class EventExecutable implements IExecutable {

    private final int weight;

    private final Class<?> eventClass;

    private Object event;

    private final IWinryContext context;

    public EventExecutable(Class<?> eventClass, int weight, IWinryContext context) {
        this.eventClass = eventClass;
        this.weight = weight;
        this.context = context;
    }

    public EventExecutable(Object event, int weight, IWinryContext context) {
        this.event = event;
        this.weight = weight;
        this.context = context;
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
        try {
            if (this.hasEvent()) {
                this.context.getEventBus().fire(this.getEvent());
            } else{
                this.context.getEventBus().fire(this.getEventClass());
            }
        } catch (EventExecutionException e) {
            this.context.getExceptionManager().toss(e);
        }
    }

    @Override
    public String getDisplay() {
        return "Event: " + this.getEventClass().getName() + " (" + this.getWeight() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventExecutable other)) {
            return false;
        }

        return other.context.equals(this.context) && other.eventClass.equals(this.eventClass) && other.weight == this.weight;
    }

    @Override
    public int hashCode() {
        return this.context.hashCode() + this.eventClass.hashCode() + this.weight;
    }
}
