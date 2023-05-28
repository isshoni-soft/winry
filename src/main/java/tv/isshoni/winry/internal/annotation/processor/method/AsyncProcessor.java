package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.concurrent.async.IAsyncManager;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.annotation.transformer.Async;
import tv.isshoni.winry.api.annotation.transformer.OnMain;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncProcessor implements IWinryAnnotationProcessor<Async> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public AsyncProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);

        LOGGER = context.getLoggerFactory().createLogger("AsyncProcessor");
    }

    @Override
    public void transformMethod(IAnnotatedMethod meta, IWrapperGenerator generator, Async annotation) {
        IAsyncManager asyncManager = this.context.get().getAsyncManager();

        LOGGER.debug("Applying transformation to: " + meta.getDisplay());

        generator.delegateMethod(meta, 0, (c, m, args, next) -> {
            try {
                Future<?> onOther = asyncManager.submit(() -> {
                    Object result = next.get();

                    if (result instanceof Future) {
                        return ((Future<?>) result).get();
                    }

                    return result;
                });

                if (Future.class.isAssignableFrom(m.getReturnType()) ||
                        (m.getReturnType().equals(Void.TYPE) && !annotation.block())) {
                    return onOther;
                }

                return onOther.get();
            } catch (InterruptedException | ExecutionException e) {
                throw Exceptions.rethrow(e);
            }
        });
    }

    @Override
    public List<Class<? extends Annotation>> getIncompatibleWith(Async annotation) {
        return new LinkedList<>() {{
            add(OnMain.class);
        }};
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
