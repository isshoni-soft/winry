package tv.isshoni.winry.internal.annotation.processor.field;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;

import java.util.Objects;

public class InjectProcessor implements IWinryAnnotationProcessor<Inject> {

    private final IWinryContext context;

    private final AraragiLogger LOGGER;

    public InjectProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = context.getLoggerFactory().createLogger("BasicFieldProcessor");
    }

    @Override
    public void executeField(BootstrappedField field, Inject inject) {
        if (!field.getTarget().hasObject()) {
            throw new IllegalStateException("Cannot inject a field into a target that is not instantiated!");
        }

        if (field.isInjected()) {
            LOGGER.warn("Attempted to re-inject field " + field.getDisplay() + "!");
            return;
        }

        Object injected;
        if (inject.value().equals(Inject.DEFAULT)) { // No perceivable change in initial functionality
            LOGGER.debug("Injecting: " + field.getTarget());
            injected = field.getTarget().getObject();
        } else {
            injected = this.context.getInjectionRegistry().getInjectedKey(inject.value());

            if (Objects.isNull(injected)) {
                injected = field.getTarget().newInstance();

                this.context.registerToContext(injected);

                this.context.getInjectionRegistry().registerInjection(inject.value(), injected);
            }
        }

        field.getBootstrapper().getContext().getElementBootstrapper().inject(field, injected);
    }
}
