package tv.isshoni.winry.entity.bootstrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public interface IBootstrappedElement<A extends Annotation, E extends AnnotatedElement> extends Comparable<IBootstrappedElement<?, ?>> {

    A getAnnotation();

    E getBootstrappedElement();

    int getWeight();

    void execute(Map<Class<?>, Object> provided);

    default int compareTo(IBootstrappedElement<?, ?> value) {
        return value.getWeight() - this.getWeight();
    }
}
