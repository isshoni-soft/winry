package tv.isshoni.winry.internal.event;

import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.entity.event.IEvent;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.event.IEventHandler;

import java.util.HashMap;

public class WinryEventHandler implements IEventHandler {

    private final BootstrappedMethod method;

    private final Listener listener;

    public WinryEventHandler(BootstrappedMethod method, Listener listener) {
        this.method = method;
        this.listener = listener;
    }

    @Override
    public void execute(IEvent event) {
        this.method.getBootstrapper().getContext().getElementBootstrapper().execute(this.method, new HashMap<>() {{
            put("event", event);
        }});
    }

    @Override
    public BootstrappedMethod getHandler() {
        return this.method;
    }

    @Override
    public Class<? extends IEvent> getTargetEvent() {
        return this.listener.value();
    }

    @Override
    public int getWeight() {
        return this.listener.weight();
    }

    @Override
    public boolean shouldIgnoreCancelled() {
        return this.listener.ignoreCancelled();
    }

    @Override
    public boolean needsMainThread() {
        return this.listener.needsMainThread();
    }
}
