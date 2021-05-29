package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.entity.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;

public interface IAnnotationManager {

    <T extends Annotation> void unregister(Class<T> annotation);

    void register(Class<? extends Annotation>[] annotations, Class<? extends IAnnotationProcessor<?>>... processors);

    void register(Class<? extends Annotation>[] annotations, IAnnotationProcessor<?>... processors);

    void register(Class<? extends Annotation> annotation, Class<? extends IAnnotationProcessor<?>>... processors);

    <T extends Annotation> void register(Class<T> annotation, IAnnotationProcessor<?>... processors);

    <A extends Annotation> int calculateWeight(Collection<A> annotations);

    List<IAnnotationProcessor<?>> get(Class<? extends Annotation> annotation);

    Collection<Class<? extends Annotation>> getManagedAnnotations();

    List<PreparedAnnotationProcessor> toExecutionList(Collection<Annotation> annotations);

    List<Annotation> getManagedAnnotationsOn(AnnotatedElement element);

    List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> getConflictingAnnotations(Collection<Annotation> annotations);

    boolean hasManagedAnnotation(AnnotatedElement element);

    <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations);

    int getTotalProcessors();
}
