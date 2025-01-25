package institute.isshoni.winry.internal.model.meta;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.winry.api.meta.IAnnotatedClass;

import java.lang.reflect.AnnotatedElement;

public interface IDeclared<E extends AnnotatedElement> extends IAnnotatedMeta<E> {

    IAnnotatedClass getDeclaringClass();

    Object getDeclaringClassInstance();

    default void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        execute(preparedAnnotationProcessor, getDeclaringClassInstance());
    }

    default void execute() {
        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .forEach(this::execute);
    }
}
