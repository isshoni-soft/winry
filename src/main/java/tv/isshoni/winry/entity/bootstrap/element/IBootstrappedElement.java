package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Set;

public interface IBootstrappedElement<E extends AnnotatedElement> extends Comparable<IBootstrappedElement<?>> {

    Collection<Annotation> getAnnotations();

    E getBootstrappedElement();

    Set<ReflectedModifier> getModifiers();

    IBootstrapper getBootstrapper();

    String getDisplay();

    default int getWeight() {
        return this.getBootstrapper().getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    void execute();

    default int compareTo(IBootstrappedElement<?> value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}
