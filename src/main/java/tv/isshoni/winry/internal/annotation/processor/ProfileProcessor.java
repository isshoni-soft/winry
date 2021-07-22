package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.annotation.Profile;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.time.Instant;

public class ProfileProcessor implements IAnnotationProcessor<Profile> {

    private static final AraragiLogger LOGGER = AraragiLogger.create("Profiling");

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, Profile annotation) {
        blueprint.registerSimpleMethodDelegator(bootstrappedMethod.getBootstrappedElement(), 1, (c, m, args, next) -> {
            Instant prev = Instant.now();

            Object result = null;
            try {
                result = next.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            LOGGER.info("Method execution: " + m.getName() + " took " + (Instant.now().toEpochMilli() - prev.toEpochMilli()) + "ms!");
            return result;
        });
    }
}
