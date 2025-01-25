package institute.isshoni.winry.internal.model.meta;

import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

public interface ITransformable<E extends AnnotatedElement> {

    E getElement();

    Set<Annotation> getAnnotations();

    IWinryContext getContext();

    void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor, IWrapperGenerator generator);

    default void transform(IWrapperGenerator generator) {
        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .stream()
                .filter(getContext().getAnnotationManager()::isWinry)
                .map(p -> (IWinryPreparedAnnotationProcessor) p)
                .forEach(p -> transform(p, generator));
    }
}
