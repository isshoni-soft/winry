package tv.isshoni.winry.entity.element;

import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IBootstrappedElement<E extends AnnotatedElement> extends Comparable<IBootstrappedElement<?>> {

    Collection<Annotation> getAnnotations();

    E getBootstrappedElement();

    Set<ReflectedModifier> getModifiers();

    AnnotationManager getAnnotationManager();

    default int getWeight() {
        return this.getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    void execute(Map<Class<?>, Object> provided);

    default int compareTo(IBootstrappedElement<?> value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}
