package tv.isshoni.winry.internal.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractAnnotatedMeta<E extends AnnotatedElement> implements IAnnotatedMeta<E> {

    protected final E element;

    protected final Set<Annotation> annotations;

    protected final IWinryContext context;

    public AbstractAnnotatedMeta(IWinryContext context, E element) {
        this.context = context;
        this.element = element;
        this.annotations = new HashSet<>();
    }

    @Override
    public void regenerate() {
        IWinryAnnotationManager annotationManager = this.context.getAnnotationManager();

        List<Annotation> annotations = annotationManager.getManagedAnnotationsOn(this.element);

        this.annotations.clear();
        this.annotations.addAll(annotations);

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.getDisplay() + " has conflicting annotations! "
                    + annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return Collections.unmodifiableSet(this.annotations);
    }

    @Override
    public E getElement() {
        return this.element;
    }
}
