package tv.isshoni.winry.internal.meta;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractAnnotatedMeta<E extends AnnotatedElement> implements IAnnotatedMeta<E> {

    protected final AraragiLogger logger;

    protected final E element;

    protected final Set<Annotation> annotations;

    protected final IWinryContext context;

    public AbstractAnnotatedMeta(IWinryContext context, E element) {
        this.context = context;
        this.element = element;
        this.logger = this.context.getLoggerFactory().createLogger(this.getClass().getSimpleName());
        this.annotations = new HashSet<>();

        refreshAnnotations();
    }

    @Override
    public void refreshAnnotations() {
        logger.debug(this.getElement() + " -- Refreshing annotations...");
        IWinryAnnotationManager annotationManager = this.context.getAnnotationManager();

        List<Annotation> annotations = annotationManager.getManagedAnnotationsOn(this.element);

        this.annotations.clear();
        this.annotations.addAll(annotations);

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.getDisplay() + " has conflicting annotations! "
                    + annotationManager.getConflictingAnnotations(this.annotations));
        }
        logger.debug(this.getElement() + " -- Found " + this.annotations.size() + " annotations!");
    }

    @Override
    public boolean hasAnnotations(Class<? extends Annotation>... annotation) {
        List<Class<? extends Annotation>> annotations = Arrays.asList(annotation);

        return Streams.to(this.annotations)
                .map(Annotation::annotationType)
                .anyMatch(annotations::contains);
    }

    @Override
    public <A extends Annotation> A getAnnotationByType(Class<A> clazz) {
        for (Annotation annotation : this.annotations) {
            if (annotation.annotationType().equals(clazz)) {
                return (A) annotation;
            }
        }

        return null;
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
