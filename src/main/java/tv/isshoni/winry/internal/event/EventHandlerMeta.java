package tv.isshoni.winry.internal.event;

import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.internal.model.event.IEventHandler;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

import java.util.HashMap;

public class EventHandlerMeta implements IEventHandler<Object> {

    private final IAnnotatedMethod method;

    private final Object target;

    private final Listener listener;

    public EventHandlerMeta(IAnnotatedMethod method, Object target, Listener listener) {
        this.method = method;
        this.listener = listener;
        this.target = target;
    }

    @Override
    public void execute(Object event) {
        this.method.getContext().getMetaManager().execute(this.method, this.target, new HashMap<>() {{
            put("event", event);
        }});
    }

    public IAnnotatedMethod getHandler() {
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