package tv.isshoni.winry.internal.annotation.processor;

import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.logging.WinryLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InjectProcessor implements IAnnotationProcessor<Inject> {

    private static final WinryLogger LOGGER = WinryLogger.create("BasicFieldProcessor");

    private static final Map<String, Object> SINGLETONS = new HashMap<>(); // the irony of this name isn't lost on me

    @Override
    public void executeField(BootstrappedField field, Inject inject) {
        if (!field.getTarget().hasObject()) {
            throw new IllegalStateException("Cannot inject a field into a target that is not instantiated!");
        }

        if (field.isInjected()) {
            LOGGER.warning("Attempted to re-inject field " + field.getDisplay() + "!");
            return;
        }

        Object injected;
        if (inject.value().equals(Inject.DEFAULT)) { // No perceivable change in initial functionality
            LOGGER.info("Injecting: " + field.getTarget());
            injected = field.getTarget().getObject();
        } else {
            injected = SINGLETONS.get(inject.value());

            if (Objects.isNull(injected)) {
                injected = field.getTarget().newInstance();

                SINGLETONS.put(inject.value(), injected);
            }
        }

        field.getBootstrapper().inject(field, injected);
    }
}
