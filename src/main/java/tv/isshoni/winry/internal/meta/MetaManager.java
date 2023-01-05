package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.internal.model.logging.ILoggerFactory;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MetaManager implements IMetaManager {

    private final AraragiLogger logger;

    private final TypeMap<Class<?>, IAnnotatedClass> storedClassMetas;

    private IWinryContext context;

    public MetaManager(ILoggerFactory loggerFactory) {
        this.logger = loggerFactory.createLogger("MetaManager");
        this.storedClassMetas = new TypeMap<>();
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
    public IAnnotatedClass generateMeta(Class<?> element) {
        return generateMeta(element, null);
    }

    @Override
    public IAnnotatedClass generateMeta(Class<?> element, Object object) {
        AnnotatedClass annotatedClass = new AnnotatedClass(this.context, element);
        annotatedClass.setInstance(object);

        if (!this.storedClassMetas.containsKey(element)) {
            this.storedClassMetas.put(element, annotatedClass);
        }

        return annotatedClass;
    }

    @Override
    public IAnnotatedMethod generateMeta(IAnnotatedClass parent, Method method) {
        return new AnnotatedMethod(this.context, parent, method);
    }

    @Override
    public IAnnotatedField generateMeta(IAnnotatedClass parent, Field field) {
        return new AnnotatedField(this.context, parent, field);
    }

    @Override
    public IAnnotatedClass getMeta(Object element) {
        Class<?> clazz = element.getClass();

        if (Class.class.isAssignableFrom(element.getClass())) {
            clazz = (Class<?>) element;
        }

        return this.storedClassMetas.get(clazz);
    }

    @Override
    public Set<IAnnotatedClass> getAllClasses() {
        return new HashSet<>(this.storedClassMetas.values());
    }

    @Override
    public void inject(IAnnotatedField meta, Object instance, Object value) {
        try {
            Object target = null;
            Field field = meta.getElement();

            logger.debug("Injecting: " + field.toString());

            if (!meta.getModifiers().contains(ReflectedModifier.STATIC)) {
                target = instance;

                if (target == null) {
                    throw new RuntimeException("Tried injecting into null instance " + meta.getDisplay());
                }
            }

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
}
