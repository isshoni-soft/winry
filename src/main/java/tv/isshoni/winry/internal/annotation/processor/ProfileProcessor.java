package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.winry.annotation.Profile;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.internal.delegator.ProfileDelegator;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class ProfileProcessor implements IAnnotationProcessor<Profile> {

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Profile annotation) {
        blueprint.registerAdvancedMethodTransformation(bootstrappedMethod.getBootstrappedElement(), (m, b, builder) -> builder
                .method(named(m.getName())
                    .and(isDeclaredBy(m.getDeclaringClass()))
                    .and(returns(m.getReturnType())))
                .intercept(to(ProfileDelegator.class)));
    }
}
