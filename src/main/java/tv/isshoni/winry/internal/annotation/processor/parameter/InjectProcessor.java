package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

// TODO: Add more than just WinryContext hardwired injections, allow a bunch of the WinryContext managers
// TODO: to be injected too.
public class InjectProcessor implements IWinryAdvancedAnnotationProcessor<Inject, Object> {

    private final IWinryContext context;

    private final AraragiLogger LOGGER;

    public InjectProcessor(@Context IWinryContext context) {
        this.context = context;

        LOGGER = context.getLoggerFactory().createLogger("BasicFieldProcessor");
    }

    @Override
    public void executeField(IAnnotatedField meta, Object target, Inject annotation) {
        Field field = meta.getElement();
        Object injected;

        injected = getInjected(annotation, Objects.requireNonNullElseGet(target, field::getType));

        if (injected == null) {
            throw new IllegalStateException("Cannot find desired injected value for: " + meta.getDisplay() + "!");
        }

        LOGGER.debug("Injecting: " + injected);
        this.context.getMetaManager().inject(meta, target, injected);
    }

    @Override
    public Object supply(Inject annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        Object injected = getInjected(annotation, parameter.getType());
        LOGGER.debug("Supplying parameter: " + injected + " for expected: " + parameter.getParameterizedType());
        return injected;
    }

    public Object getInjected(Inject annotation, Object target) {
        return getInjected(annotation, this.context.getMetaManager().getMeta(target.getClass()));
    }

    public Object getInjected(Inject annotation, IAnnotatedClass classMeta) {
        Class<?> clazz = classMeta.getElement();
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
            injected = getInjected(annotation.value(), clazz);

            if (Objects.isNull(injected)) {
                try {
                    injected = classMeta.newInstance();
                } catch (Throwable e) {
                    this.context.getExceptionManager().toss(e);
                }

                this.context.registerToContext(injected);
                this.context.getInstanceManager().registerKeyedInstance(classMeta, annotation.value(), injected);
            }
        }

        return injected;
    }

    public Object getInjected(Class<?> clazz) {
        return this.context.getInstanceManager().getSingletonInjection(clazz);
    }

    public Object getInjected(String key, Class<?> clazz) {
        return this.context.getInstanceManager().getKeyedInstance(key, clazz);
    }
}
