package tv.isshoni.winry.api.entity.event;

public abstract class WinryEvent implements IEvent {

    private final String name;

    private final boolean async;

    public WinryEvent(String name, boolean async) {
        this.name = name;
        this.async = async;
    }

    @Override
    public boolean isAsync() {
        return this.async;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
