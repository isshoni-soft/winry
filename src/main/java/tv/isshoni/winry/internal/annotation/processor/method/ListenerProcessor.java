package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

public class ListenerProcessor implements IWinryAnnotationProcessor<Listener> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public ListenerProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = this.context.getLoggerFactory().createLogger("ListenerProcessor");
    }

    @Override
    public void executeMethod(IAnnotatedMethod method, Object target, Listener annotation) {
        LOGGER.debug("Register listener for: " + annotation.value().getName() + " - " + method.getDisplay());
        this.context.getEventBus().registerListener(method, target, annotation);
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }
}
