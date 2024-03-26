package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.ILoggerFactory;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedField;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.api.meta.ISingletonAnnotatedClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MetaManager implements IMetaManager {

    private final AraragiLogger logger;

    private final TypeMap<Class<?>, IAnnotatedClass> classes;
    private final TypeMap<Class<?>, ISingletonAnnotatedClass> singletons;

    private IWinryContext context;

    public MetaManager(ILoggerFactory loggerFactory) {
        this.logger = loggerFactory.createLogger("MetaManager");
        this.classes = new TypeMap<>();
        this.singletons = new TypeMap<>();
    }

    @Override
    public void setContext(IWinryContext context) {
        this.context = context;
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public ISingletonAnnotatedClass generateSingletonMeta(Class<?> element) throws Throwable {
        return generateSingletonMeta(element, null);
    }

    @Override
    public ISingletonAnnotatedClass generateSingletonMeta(Class<?> element, Object object) throws Throwable {
        SingletonAnnotatedClass annotatedClass;
        if (object == null) {
            annotatedClass = new SingletonAnnotatedClass(this.context, element);
        } else {
            annotatedClass = new SingletonAnnotatedClass(this.context, element, object);
        }

        if (!this.singletons.containsKey(element)) {
            this.singletons.put(element, annotatedClass);
        }

        return annotatedClass;
    }

    @Override
    public IAnnotatedClass generateMeta(Class<?> element) {
        AnnotatedClass annotatedClass = new AnnotatedClass(this.context, element);

        if (!this.classes.containsKey(element)) {
            this.classes.put(element, annotatedClass);
        }

        return annotatedClass;
    }

    @Override
    public ISingletonAnnotatedClass getSingletonMeta(Object element) {
        return this.singletons.get(resolveClass(element));
    }

    @Override
    public IAnnotatedClass getMeta(Object element) {
        return this.classes.get(resolveClass(element));
    }

    @Override
    public IAnnotatedClass findMeta(Object element) {
        IAnnotatedClass result = getSingletonMeta(element);

        if (result == null) {
            result = getMeta(element);
        }

        return result;
    }

    @Override
    public Set<ISingletonAnnotatedClass> getAllSingletonClasses() {
        return new HashSet<>(this.singletons.values());
    }

    @Override
    public void inject(IAnnotatedField meta, Object instance, Object value) {
        try {
            Object target = null;
            Field field = meta.getElement();

            logger.debug("Injecting: " + field.toString());
            logger.debug("-> Modifiers: " + meta.getModifiers().toString());

            if (!meta.getModifiers().contains(ReflectedModifier.STATIC)) {
                target = instance;

                if (target == null) {
                    throw new RuntimeException("Tried injecting into null instance " + meta.getDisplay());
                }
            }

            logger.debug("-> Target: " + target);
            logger.debug("-> Value: " + (value == null ? "null" : value.toString()));
            logger.debug("-> Field: " + field);
            ReflectionUtil.injectField(field, target, value);
        } catch (Throwable t) {
            this.context.getExceptionManager().toss(t);
        }
    }

    @Override
    public <R> R execute(IAnnotatedMethod meta, Object instance, Map<String, Object> runtimeContext) {
        Method method = meta.getElement();
        Object target = null;

        if (!meta.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = instance;

            if (Objects.isNull(target)) {
                logger.error("Non-static target is null for: " + meta.getDisplay());
            }
        }

        logger.debug("Executing: " + meta.getDeclaringClass().getDisplay() + " -- " + method);
        try {
            return this.context.getAnnotationManager().execute(method, target, runtimeContext);
        } catch (Throwable e) {
            this.context.getExceptionManager().toss(e, method);
            return null;
        }
    }

    private Class<?> resolveClass(Object element) {
        Class<?> clazz = element.getClass();

        if (Class.class.isAssignableFrom(element.getClass())) {
            clazz = (Class<?>) element;
        }

        return clazz;
    }
}
