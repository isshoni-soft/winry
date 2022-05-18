package tv.isshoni.winry.api.entity.event;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.event.IEventBus;
import tv.isshoni.winry.entity.logging.ILoggerFactory;
import tv.isshoni.winry.reflection.ReflectionUtil;

public class WinryEventExecutable<T extends IEvent> implements IExecutable {

    private final AraragiLogger LOGGER;

    private final int weight;

    private final Class<T> eventClass;

    private final T event;

    private final IEventBus bus;

    public WinryEventExecutable(Class<T> eventClass, int weight, IWinryContext context) {
        this.eventClass = eventClass;
        this.weight = weight;
        this.bus = context.getEventBus();
        this.event = ReflectionUtil.construct(eventClass);
        this.LOGGER = context.getLoggerFactory().createLogger("EventExecutable");
    }

    public WinryEventExecutable(T event, int weight, IWinryContext context) {
        this.event = event;
        this.weight = weight;
        this.bus = context.getEventBus();
        this.eventClass = (Class<T>) event.getClass();
        this.LOGGER = context.getLoggerFactory().createLogger("EventExecutable");
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

    @Override
    public void execute() {
        this.LOGGER.debug("Executing event: " + this.getEventClass().getName());
        this.bus.fire(this.getEvent());
    }

    @Override
    public String getDisplay() {
        return "Event: " + this.getEventClass().getName() + " (" + this.getWeight() + ")";
    }
}
