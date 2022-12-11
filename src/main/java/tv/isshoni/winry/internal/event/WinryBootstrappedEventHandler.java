package tv.isshoni.winry.internal.event;

import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.event.IEventHandler;

import java.util.HashMap;

public class WinryBootstrappedEventHandler implements IEventHandler<Object> {

    private final BootstrappedMethod method;

    private final Listener listener;

    public WinryBootstrappedEventHandler(BootstrappedMethod method, Listener listener) {
        this.method = method;
        this.listener = listener;
    }

    @Override
    public void execute(Object event) {
        this.method.getBootstrapper().getContext().getElementBootstrapper().execute(this.method, new HashMap<>() {{
            put("event", event);
        }});
    }

    public BootstrappedMethod getHandler() {
        return this.method;
    }

    @Override
    public Class<?> getTargetEvent() {
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
