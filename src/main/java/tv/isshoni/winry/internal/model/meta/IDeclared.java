package tv.isshoni.winry.internal.model.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.meta.IAnnotatedClass;

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
