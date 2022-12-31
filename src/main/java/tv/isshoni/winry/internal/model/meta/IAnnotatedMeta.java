package tv.isshoni.winry.internal.model.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.bootstrap.IExecutable;
import tv.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

public interface IAnnotatedMeta<E extends AnnotatedElement> extends IExecutable {

    void regenerate();

    void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor);

    Set<Annotation> getAnnotations();

    E getElement();

    Set<ReflectedModifier> getModifiers();

    IWinryContext getContext();

    default int getWeight() {
        return getContext().getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    default void execute() {
        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .forEach(this::execute);
    }
}
