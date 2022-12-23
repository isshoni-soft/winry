package tv.isshoni.winry.internal.model.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

public interface ITransformable<E extends AnnotatedElement> {

    boolean isTransformed();

    E getElement();

    Set<Annotation> getAnnotations();

    IWinryContext getContext();

    void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor);

    default void transform() {
        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .stream()
                .filter(getContext().getAnnotationManager()::isWinry)
                .map(p -> (IWinryPreparedAnnotationProcessor) p)
                .forEach(this::transform);
    }
}
