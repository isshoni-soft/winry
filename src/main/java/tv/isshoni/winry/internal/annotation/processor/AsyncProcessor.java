package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsyncProcessor implements IAnnotationProcessor<Async> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final AraragiLogger LOGGER = AraragiLogger.create("AsyncProcessor");

    // TODO: Move this stuff to Araragi when you get the chance, just make a universal AsyncManager class & maybe give it it's own module.
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Waiting on async executor...");
            LOGGER.info("If it is apparent that a thread is deadlocked, please force kill...");

            EXECUTOR_SERVICE.shutdown();
            try {
                EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })); // Pls java let us addShutdownHook(Runnable)
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Async annotation) {
        Method method = bootstrappedMethod.getBootstrappedElement();
        LOGGER.info("Running on " + bootstrappedMethod);

        if (!(method.getReturnType().isAssignableFrom(Future.class) || method.getReturnType().equals(Void.TYPE))) {
            // TODO: Make specialized exception for this
            throw new RuntimeException("Tried to make async method with non-void/future return type!");
        }

        blueprint.registerSimpleMethodDelegator(bootstrappedMethod.getBootstrappedElement(), 0, (c, m, args, next) ->
                EXECUTOR_SERVICE.submit(() -> {
                    Object result = next.call();

                    if (result instanceof Future) {
                        return ((Future<?>) result).get();
                    }

                    return result;
                }));
    }
}
