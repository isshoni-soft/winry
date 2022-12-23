package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

public class InjectProcessor implements IWinryAdvancedAnnotationProcessor<Inject, Object> {

    private final IWinryContext context;

    private final AraragiLogger LOGGER;

    public InjectProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = context.getLoggerFactory().createLogger("BasicFieldProcessor");
    }

    @Override
    public void executeField(BootstrappedField field, Inject inject) {
        Object injected;
        if (field.getTarget() == null) {
            injected = getInjected(inject, field.getBootstrappedElement().getType());
        } else {
            injected = getInjected(inject, field.getTarget());
        }

        if (injected == null) {
            throw new IllegalStateException("Cannot find desired injected value for: " + field.getTarget().getDisplay() + "!");
        }

        if (field.isInjected()) {
            LOGGER.warn("Attempted to re-inject field " + field.getDisplay() + "!");
            return;
        }

        LOGGER.debug("Injecting: " + injected);
        field.getBootstrapper().getContext().getElementBootstrapper().inject(field, injected);
    }

    @Override
    public Object supply(Inject annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        Object injected = getInjected(annotation, parameter.getType());
        LOGGER.debug("Supplying parameter: " + injected + " for expected: " + parameter.getParameterizedType());
        return injected;
    }

    public Object getInjected(Inject annotation, BootstrappedClass clazz) {
        return getInjected(annotation, clazz.getBootstrappedElement());
    }

    public Object getInjected(Inject annotation, Class<?> clazz) {
        Object injected;

        LOGGER.debug("Getting injected for type: " + clazz);
        if (IWinryContext.class.isAssignableFrom(clazz) || clazz.equals(IWinryContext.class)) {
            LOGGER.debug("Injecting context...");
            return this.context;
        }
        LOGGER.debug("Injecting other....");

        if (annotation.value().equals(Inject.DEFAULT)) { // No perceivable change in initial functionality
            injected = getInjected(clazz);
        } else {
            injected = getInjected(annotation.value());

            if (Objects.isNull(injected)) {
                injected = this.context.getElementBootstrapper().getBootstrappedClass(clazz).newInstance();

                this.context.registerToContext(injected);

                this.context.getInjectionRegistry().registerInjection(annotation.value(), injected);
            }
        }

        return injected;
    }

    public Object getInjected(Class<?> clazz) {
        return this.context.getInjectionRegistry().getSingletonInjection(clazz);
    }

    public Object getInjected(String key) {
        return this.context.getInjectionRegistry().getInjectedKey(key);
    }
}
