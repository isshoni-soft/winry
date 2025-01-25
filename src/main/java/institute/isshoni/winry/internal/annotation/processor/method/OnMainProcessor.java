package institute.isshoni.winry.internal.annotation.processor.method;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.exception.Exceptions;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.annotation.transformer.Async;
import institute.isshoni.winry.api.annotation.transformer.OnMain;
import institute.isshoni.winry.api.async.IWinryAsyncManager;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OnMainProcessor implements IWinryAnnotationProcessor<OnMain> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public OnMainProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);

        LOGGER = context.getLoggerFactory().createLogger("OnMainProcessor");
    }

    @Override
    public void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator, OnMain annotation) {
        IWinryAsyncManager asyncManager = this.context.get().getAsyncManager();

        LOGGER.debug("Applying transformation to: " + method.getDisplay());

        generator.delegateMethod(method, 0, (c, m, args, next) -> {
            try {
                Future<?> onMain = asyncManager.submitToMain(() -> {
                    Object result = next.get();

                    if (result instanceof Future) {
                        return ((Future<?>) result).get();
                    }

                    return result;
                });

                if (Future.class.isAssignableFrom(m.getReturnType()) ||
                        (m.getReturnType().equals(Void.TYPE) && !annotation.block())) {
                    return onMain;
                }

                return onMain.get();
            } catch (InterruptedException | ExecutionException e) {
                throw Exceptions.rethrow(e);
            }
        });
    }

    @Override
    public List<Class<? extends Annotation>> getIncompatibleWith(OnMain annotation) {
        return new LinkedList<>() {{
            add(Async.class);
        }};
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
