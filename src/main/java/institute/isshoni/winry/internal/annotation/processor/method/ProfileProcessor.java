package institute.isshoni.winry.internal.annotation.processor.method;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.annotation.transformer.Profile;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.time.Instant;

public class ProfileProcessor implements IWinryAnnotationProcessor<Profile> {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public ProfileProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        LOGGER = context.getLoggerFactory().createLogger("Profiling");
    }

    @Override
    public void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator, Profile annotation) {
        generator.delegateMethod(method, 1, (c, m, args, next) -> {
            Instant prev = Instant.now();

            Object result = next.get();

            LOGGER.debug("Method execution: " + m.getName() + " took " + (Instant.now().toEpochMilli() - prev.toEpochMilli()) + "ms!");
            return result;
        });
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
