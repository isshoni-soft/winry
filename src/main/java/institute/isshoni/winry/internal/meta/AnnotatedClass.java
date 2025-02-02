package institute.isshoni.winry.internal.meta;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.reflect.ReflectedModifier;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.meta.IAnnotatedField;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import institute.isshoni.winry.internal.model.meta.ITransformable;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

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

    public AnnotatedClass(IWinryContext context, Class<?> element) {
        super(context, element);
        this.modifiers = ReflectedModifier.getModifiers(element);
        this.methods = new HashMap<>();
        this.fields = new HashMap<>();
    }

    @Override
    public void regenerate(Object object) {
        super.refreshAnnotations();

        this.methods.clear();
        this.fields.clear();

        logger.debug(getDisplay() + " -- Building...");

        Streams.to(getElement().getDeclaredMethods())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(m -> new AnnotatedMethod(this.context, this, object, m))
                .forEach(m -> this.methods.put(m.getElement(), m));
        logger.debug("Discovered " + getMethods().size() + " methods");

        Streams.to(getElement().getDeclaredFields())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(f -> new AnnotatedField(this.context, this, object, f))
                .forEach(f -> this.fields.put(f.getElement(), f));
        logger.debug("Discovered " + getMethods().size() + " fields");

        logger.debug(getDisplay() + " -- Finished Building...");
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor, Object target) {
        preparedAnnotationProcessor.executeClass(target);
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
    public Object newInstance(Object... parameters) throws Throwable {
        if (this.transformed != null) {
            return this.context.getAnnotationManager().construct(this.transformed, parameters);
        } else {
            return this.context.getAnnotationManager().construct(this.element, parameters);
        }
    }

    @Override
    public void transform(IWrapperGenerator generator) {
        logger.debug(this.getDisplay() + " -- Transforming...");
        this.regenerate(null);
        IAnnotatedClass.super.transform(generator);

        logger.debug("-> Transforming: ${0} methods...", getMethods().size());
        Streams.to(getMethods())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(t -> t.transform(generator));

        logger.debug("-> Transforming: ${0} fields...", getFields().size());
        Streams.to(getFields())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(t -> t.transform(generator));

        this.transformed = generator.generate();

        logger.debug(this.getDisplay() + " -- Transforming Complete!");
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
    public void execute() {
        throw new UnsupportedOperationException("Please use AnnotatedClass#execute(Object)");
    }

    @Override
    public String getDisplay() {
        return "Class: " + this.element.getSimpleName() + " [" + this.annotations + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IAnnotatedClass other)) {
            return false;
        }

        return other.getElement().equals(this.element) && other.isTransformed() == this.isTransformed()
                && (this.getTransform() == null ? this.getTransform() == other.getTransform() : this.getTransform().equals(other.getTransform()));
    }

    @Override
    public int hashCode() {
        return this.element.hashCode();
    }
}
