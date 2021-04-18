package tv.isshoni.winry.bootstrap.element;

import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Set;

public interface IBootstrappedElement<A extends Annotation, E extends AnnotatedElement> extends Comparable<IBootstrappedElement<?, ?>> {

    A getAnnotation();

    E getBootstrappedElement();

    Set<ReflectedModifier> getModifiers();

    int getWeight();

    void execute(Map<Class<?>, Object> provided);

    default int compareTo(IBootstrappedElement<?, ?> value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}
