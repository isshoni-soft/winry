package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.ITransformedClass;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AnnotatedClass implements IAnnotatedMeta<Class<?>>, ITransformedClass {

    protected final IWinryContext context;

    protected final Set<Annotation> annotations;

    protected final Set<ReflectedModifier> modifiers;

    protected final Class<?> element;

    protected Class<?> transformed;

    public AnnotatedClass(IWinryContext context, Class<?> element) {
        this.context = context;
        this.element = element;
        this.modifiers = ReflectedModifier.getModifiers(element);
        this.annotations = new HashSet<>();
    }

    public Object newInstance() {
        return getContext().getMetaManager().construct(this, true);
    }

    @Override
    public void reloadAnnotations() {
        IWinryAnnotationManager annotationManager = this.context.getAnnotationManager();

        List<Annotation> annotations = annotationManager.getManagedAnnotationsOn(this.element);

        this.annotations.clear();
        this.annotations.addAll(annotations);

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.element.getSimpleName() + " has conflicting annotations! "
                    + annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeClass(this.getElement());
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor) {
//        preparedAnnotationProcessor.transformClass();
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return Collections.unmodifiableSet(this.annotations);
    }

    @Override
    public Class<?> getElement() {
        return this.element;
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
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public boolean isTransformed() {
        return Objects.nonNull(this.transformed);
    }

    public boolean isDirty() {
        return true;
    }
}
