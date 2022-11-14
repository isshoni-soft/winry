package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.winry.api.annotation.parameter.Event;
import tv.isshoni.winry.api.event.IEvent;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

public class EventProcessor implements IParameterSupplier<Event, IEvent> {

    @Override
    public IEvent supply(Event event, IEvent o, Parameter parameter, Map<String, Object> runtimeContext) {
        if (Objects.nonNull(o)) {
            return o;
        }

        Object v = runtimeContext.get("event");

        if (Objects.nonNull(v)) {
            return (IEvent) v;
        }

        return null;
    }
}
