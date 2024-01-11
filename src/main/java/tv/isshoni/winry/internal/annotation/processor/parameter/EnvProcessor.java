package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Env;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedField;

import java.lang.reflect.Parameter;

public class EnvProcessor implements IWinryAdvancedAnnotationProcessor<Env, String> {

    private final Constant<IWinryContext> context;

    private final AraragiLogger LOGGER;

    public EnvProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        LOGGER = context.getLoggerFactory().createLogger("BasicFieldProcessor");
    }

    @Override
    public String supply(Env annotation, String previous, Parameter parameter) {
        return getEnv(annotation);
    }

    @Override
    public void executeField(IAnnotatedField field, Object target, Env annotation) {
        Object injected = getEnv(annotation);

        LOGGER.debug("Injecting: " + injected);
        this.context.get().getMetaManager().inject(field, target, injected);
    }

    public String getEnv(Env annotation) {
        return System.getenv(annotation.value());
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
