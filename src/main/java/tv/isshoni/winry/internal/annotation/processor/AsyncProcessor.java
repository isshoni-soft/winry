package tv.isshoni.winry.internal.annotation.processor;

import net.bytebuddy.implementation.bind.annotation.Pipe;
import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.internal.delegator.AsyncDelegator;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.logging.WinryLogger;

import java.util.function.Function;

import static net.bytebuddy.implementation.MethodDelegation.withDefaultConfiguration;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class AsyncProcessor implements IAnnotationProcessor<Async> {

    private static final WinryLogger LOGGER = WinryLogger.create("AsyncProcessor");

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Async annotation) {
        LOGGER.info("Running on " + bootstrappedMethod);

        blueprint.registerAdvancedMethodTransformation(bootstrappedMethod.getBootstrappedElement(), (m, e, builder) -> builder
                .method(named(m.getName())
                        .and(isDeclaredBy(m.getDeclaringClass()))
                        .and(returns(m.getReturnType())))
                .intercept(withDefaultConfiguration()
                        .withBinders(Pipe.Binder.install(Function.class))
                        .to(AsyncDelegator.class)));
//                        .andThen(to(ProfileDelegator.class))
//                        .andThen(invokeSelf())));
    }
}
