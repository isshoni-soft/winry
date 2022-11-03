package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

public class ListenerProcessor implements IWinryAnnotationProcessor<Listener> {

    private final AraragiLogger LOGGER;

    private final IWinryContext context;

    public ListenerProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = this.context.getLoggerFactory().createLogger("ListenerProcessor");
    }

    @Override
    public void executeMethod(BootstrappedMethod method, Listener annotation) {
        LOGGER.debug("Register listener for: " + annotation.value().getName() + " - " + method.getDisplay());
        this.context.getEventBus().registerListener(method, annotation);
    }
}
