package institute.isshoni.winry.internal.model.meta;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.araragi.reflect.ReflectedModifier;
import institute.isshoni.winry.api.bootstrap.executable.IExecutable;
import institute.isshoni.winry.api.context.IWinryContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

public interface IAnnotatedMeta<E extends AnnotatedElement> extends IExecutable {

    void refreshAnnotations();

    void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor, Object target);

    boolean hasAnnotations(Class<? extends Annotation>... annotation);

    <A extends Annotation> A getAnnotationByType(Class<A> clazz);

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
