package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.ITransformable;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotatedClass extends AbstractAnnotatedMeta<Class<?>> implements IAnnotatedClass {

    protected final Set<ReflectedModifier> modifiers;

    protected Class<?> transformed;

    protected final Map<Method, IAnnotatedMethod> methods;

    protected final Map<Field, IAnnotatedField> fields;

    protected Object instance;

    public AnnotatedClass(IWinryContext context, Class<?> element) {
        super(context, element);
        this.modifiers = ReflectedModifier.getModifiers(element);
        this.methods = new HashMap<>();
        this.fields = new HashMap<>();
    }

    public void setInstance(Object instance) {
        if (this.instance != null) {
            return;
        }

        this.instance = instance;
    }

    @Override
    public void regenerate() {
        super.regenerate(); // regenerate annotations

        this.methods.clear();
        this.fields.clear();

        logger.debug(getDisplay() + " -- Building...");

        Streams.to(getElement().getDeclaredMethods())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(m -> this.context.getMetaManager().generateMeta(this, m))
                .forEach(m -> this.methods.put(m.getElement(), m));
        logger.debug("Discovered " + getMethods().size() + " methods");

        Streams.to(getElement().getDeclaredFields())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(f -> this.context.getMetaManager().generateMeta(this, f))
                .forEach(f -> this.fields.put(f.getElement(), f));
        logger.debug("Discovered " + getMethods().size() + " fields");

        logger.debug(getDisplay() + " -- Finished Building...");
    }

    @Override
    public List<IAnnotatedMethod> getMethods() {
        return new LinkedList<>(this.methods.values());
    }

    @Override
    public List<IAnnotatedField> getFields() {
        return new LinkedList<>(this.fields.values());
    }

    @Override
    public IAnnotatedMethod getMethod(Method method) {
        return this.methods.get(method);
    }

    @Override
    public IAnnotatedMethod getMethod(String name) {
        return Streams.to(this.methods)
                .find(p -> p.getFirst().getName().equals(name))
                .map(Pair.second())
                .orElse(null);
    }

    @Override
    public IAnnotatedField getField(Field field) {
        return this.fields.get(field);
    }

    @Override
    public IAnnotatedField getField(String name) {
        return Streams.to(this.fields)
                .find(p -> p.getFirst().getName().equals(name))
                .map(Pair.second())
                .orElse(null);
    }

    @Override
    public Object getInstance() {
        if (this.instance == null) {
            try {
                this.instance = newInstance();
            } catch (Throwable e) {
                this.context.getExceptionManager().toss(e);
                return null;
            }
        }

        return this.instance;
    }

    @Override
    public Object newInstance() throws Throwable {
        if (this.transformed != null) {
            return this.context.getAnnotationManager().construct(this.transformed);
        } else {
            return this.context.getAnnotationManager().construct(this.element);
        }
    }

    @Override
    public void transform(IWrapperGenerator generator) {
        logger.debug(this.getDisplay() + " -- Transforming...");
        IAnnotatedClass.super.transform(generator);

        Streams.to(getMethods())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(t -> t.transform(generator));

        Streams.to(getFields())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(t -> t.transform(generator));

        this.transformed = generator.generate();

        logger.debug(this.getDisplay() + " -- Transforming Complete!");
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeClass(this.getInstance());
    }

    @Override
    public Class<?> getTransform() {
        return this.transformed;
    }

    @Override
    public boolean isTransformed() {
        return this.transformed != null;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor, IWrapperGenerator generator) {
        preparedAnnotationProcessor.transformClass(this, generator);
    }

    @Override
    public String getDisplay() {
        return "Class: " + this.element.getSimpleName() + " [" + this.annotations + "]";
    }
}
