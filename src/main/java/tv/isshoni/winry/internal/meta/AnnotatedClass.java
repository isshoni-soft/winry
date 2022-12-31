package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.ITransformable;
import tv.isshoni.winry.internal.model.meta.ITransformedClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AnnotatedClass extends AbstractAnnotatedMeta<Class<?>> implements ITransformedClass, IAnnotatedClass {

    protected final Set<ReflectedModifier> modifiers;

    protected Class<?> transformed;

    protected final List<IAnnotatedMeta<Method>> methods;

    protected final List<IAnnotatedMeta<Field>> fields;

    public AnnotatedClass(IWinryContext context, Class<?> element) {
        super(context, element);
        this.modifiers = ReflectedModifier.getModifiers(element);
        this.methods = new LinkedList<>();
        this.fields = new LinkedList<>();
    }

    @Override
    public List<IAnnotatedMeta<Method>> getMethods() {
        return this.methods;
    }

    @Override
    public List<IAnnotatedMeta<Field>> getFields() {
        return this.fields;
    }

    @Override
    public <R> R newInstance() throws Throwable {
        return (R) this.context.getAnnotationManager().construct(this.element);
    }

    @Override
    public void transform() {
        ITransformedClass.super.transform();

        Streams.to(getMethods())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(ITransformable::transform);

        Streams.to(getFields())
                .filter(m -> ITransformable.class.isAssignableFrom(m.getClass()))
                .cast(ITransformable.class)
                .forEach(ITransformable::transform);
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeClass(this.getElement());
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor) {
//        preparedAnnotationProcessor.transformClass(this, );
    }

    @Override
    public Class<?> getTransform() {
        return this.transformed;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    public boolean isTransformed() {
        return Objects.nonNull(this.transformed);
    }

    @Override
    public String getDisplay() {
        return this.element.getSimpleName();
    }
}
