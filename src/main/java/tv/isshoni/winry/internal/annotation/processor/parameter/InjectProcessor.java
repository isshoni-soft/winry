package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.context.IExceptionManager;
import tv.isshoni.winry.api.context.IInstanceManager;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedField;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class InjectProcessor implements IWinryAdvancedAnnotationProcessor<Inject, Object> {

    private final Constant<IWinryContext> context;

    private final AraragiLogger LOGGER;

    private final TypeMap<Class<?>, Supplier<Object>> basicSuppliers;

    public InjectProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        this.basicSuppliers = new TypeMap<>();

        LOGGER = context.getLoggerFactory().createLogger("BasicFieldProcessor");

        this.basicSuppliers.put(ILoggerFactory.class, this.context.get()::getLoggerFactory);
        this.basicSuppliers.put(IExceptionManager.class, this.context.get()::getExceptionManager);
        this.basicSuppliers.put(IEventBus.class, this.context.get()::getEventBus);
        this.basicSuppliers.put(IInstanceManager.class, this.context.get()::getInstanceManager);
    }

    @Override
    public void executeField(IAnnotatedField meta, Object target, Inject annotation) {
        Field field = meta.getElement();
        Object injected;

        injected = getInjected(annotation, field.getType());

        if (injected == null) {
            throw new IllegalStateException("Cannot find desired injected value for: " + meta.getDisplay() + "!");
        }

        LOGGER.debug("Injecting: " + injected);
        this.context.get().getMetaManager().inject(meta, target, injected);
    }

    @Override
    public Object supply(Inject annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        Object injected = getInjected(annotation, parameter.getType());
        LOGGER.debug("Supplying parameter: " + injected + " for expected: " + parameter.getParameterizedType());
        return injected;
    }

    public Object getInjected(Inject annotation, Class<?> type) {
        Optional<Supplier<Object>> presupplied = Optional.ofNullable(this.basicSuppliers.get(type));

        if (presupplied.isPresent()) {
            return presupplied.get().get();
        }

        IAnnotatedClass classMeta = this.context.get().getMetaManager().getSingletonMeta(type);

        if (classMeta == null) {
            LOGGER.debug("Cannot find classMeta to inject for: " + type);
            return null;
        }

        return getInjected(annotation, classMeta);
    }

    public Object getInjected(Inject annotation, IAnnotatedClass classMeta) {
        Class<?> clazz = classMeta.getElement();
        Object injected;

        LOGGER.debug("Getting injected for type: " + clazz);
        if (IWinryContext.class.isAssignableFrom(clazz) || clazz.equals(IWinryContext.class)) {
            LOGGER.debug("Injecting context...");
            return this.context.get();
        }
        LOGGER.debug("Injecting other....");

        if (annotation.value().equals(Inject.DEFAULT)) { // No perceivable change in initial functionality
            injected = getInjected(clazz);
        } else {
            LOGGER.debug("Injecting instance with key: " + annotation.value());
            injected = getInjected(annotation.value(), clazz);

            if (Objects.isNull(injected)) {
                LOGGER.debug("Generating new instance...");
                try {
                    injected = classMeta.newInstance();
                } catch (Throwable e) {
                    this.context.get().getExceptionManager().toss(e);
                    return null;
                }

                this.context.get().registerToContext(injected);
                this.context.get().getInstanceManager().registerKeyedInstance(classMeta, annotation.value(), injected);
            }
        }

        return injected;
    }

    public Object getInjected(Class<?> clazz) {
        return this.context.get().getInstanceManager().getSingletonInjection(clazz);
    }

    public Object getInjected(String key, Class<?> clazz) {
        return this.context.get().getInstanceManager().getKeyedInstance(key, clazz);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
