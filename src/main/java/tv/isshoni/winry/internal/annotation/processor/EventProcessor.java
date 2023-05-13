package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

public class EventProcessor implements IWinryAdvancedAnnotationProcessor<Event, Object> {

    private final Constant<IWinryContext> context;

    public EventProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
    }

    @Override
    public void executeClass(IAnnotatedClass classMeta, Object target, Event annotation) {
        if (!annotation.executable()) {
            return;
        }

        this.context.get().getEventBus().provideExecutable(this.context.get(), classMeta.getElement(), annotation.weight());
    }

    @Override
    public Object supply(Event event, Object o, Parameter parameter, Map<String, Object> runtimeContext) {
        if (Objects.nonNull(o)) {
            return o;
        }

        Object v = runtimeContext.get("event");

        if (Objects.nonNull(v)) {
            return v;
        }

        return null;
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
