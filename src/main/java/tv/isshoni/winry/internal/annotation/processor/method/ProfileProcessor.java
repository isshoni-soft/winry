package tv.isshoni.winry.internal.annotation.processor.method;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Profile;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.time.Instant;

public class ProfileProcessor implements IWinryAnnotationProcessor<Profile> {

    private final AraragiLogger LOGGER;

    public ProfileProcessor(@Context IWinryContext context) {
        LOGGER = context.getLoggerFactory().createLogger("Profiling");
    }

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

            LOGGER.debug("Method execution: " + m.getName() + " took " + (Instant.now().toEpochMilli() - prev.toEpochMilli()) + "ms!");
            return result;
        });
    }
}
