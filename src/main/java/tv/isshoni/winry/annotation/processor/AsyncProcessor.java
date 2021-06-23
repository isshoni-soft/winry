package tv.isshoni.winry.annotation.processor;

import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.bytebuddy.AsyncDelegator;
import tv.isshoni.winry.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.logging.WinryLogger;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class AsyncProcessor implements IAnnotationProcessor<Async> {

    private static final WinryLogger LOGGER = WinryLogger.create("AsyncProcessor");

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Async annotation) {
        LOGGER.info("Running on " + bootstrappedMethod);

        blueprint.registerMethodTransformation(bootstrappedMethod.getBootstrappedElement(), (m, e, builder) -> builder
                .method(named(m.getName())
                        .and(isDeclaredBy(m.getDeclaringClass()))
                        .and(returns(m.getReturnType())))
                .intercept(to(AsyncDelegator.class)));
    }
}
