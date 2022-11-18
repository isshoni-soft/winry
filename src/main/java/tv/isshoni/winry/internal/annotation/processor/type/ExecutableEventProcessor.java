package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.winry.api.annotation.ExecutableEvent;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.IEvent;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;

public class ExecutableEventProcessor implements IWinryAnnotationProcessor<ExecutableEvent> {

    private final IWinryContext context;

    public ExecutableEventProcessor(@Context IWinryContext context) {
        this.context = context;
    }

    @Override
    public void executeClass(BootstrappedClass clazz, ExecutableEvent annotation) {
        this.context.getEventBus().registerExecutable((Class<? extends IEvent>) clazz.getBootstrappedElement(), annotation.value());
    }
}
