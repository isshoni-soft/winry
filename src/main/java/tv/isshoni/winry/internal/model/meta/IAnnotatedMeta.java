package tv.isshoni.winry.internal.model.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.bootstrap.executable.IExecutable;
import tv.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

public interface IAnnotatedMeta<E extends AnnotatedElement> extends IExecutable {

    void refreshAnnotations();

    void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor, Object target);

    boolean hasAnnotation(Class<? extends Annotation> annotation);

    Set<Annotation> getAnnotations();

    E getElement();

    Set<ReflectedModifier> getModifiers();

    IWinryContext getContext();

    default int getWeight() {
        return getContext().getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    default void execute(Object target) {
        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .forEach(proc -> execute(proc, target));
    }
}
