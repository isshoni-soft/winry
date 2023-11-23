package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.IListener;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;

public class ListenerProcessor implements IWinryAnnotationProcessor<Listener> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public ListenerProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);

        LOGGER = this.context.get().getLoggerFactory().createLogger("ListenerProcessor");
    }

    @Override
    public void executeMethod(IAnnotatedMethod method, Object target, Listener annotation) {
        if (!this.context.get().getAnnotationManager().hasAnnotationWithMarker(target)
                && !(target instanceof IListener)) {
            LOGGER.error("Cannot register listener for ${0}!", method.getDisplay());
            throw new IllegalStateException(method.getDeclaringClass().getElement().getName() +
                    " must have an annotation marked with @SingletonHolder or implement IListenerObject");
        }

        LOGGER.debug("Register listener for: " + annotation.value().getName() + " - " + method.getDisplay());
        this.context.get().getEventBus().registerListener(method, target, annotation);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
