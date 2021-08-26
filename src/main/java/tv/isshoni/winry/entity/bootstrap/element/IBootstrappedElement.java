package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface IBootstrappedElement<E extends AnnotatedElement> extends Comparable<IBootstrappedElement<?>> {

    Collection<Annotation> getAnnotations();

    E getBootstrappedElement();

    Set<ReflectedModifier> getModifiers();

    IBootstrapper getBootstrapper();

    String getDisplay();

    default int getWeight() {
        return this.getBootstrapper().getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    default void execute() {
        getBootstrapper().getAnnotationManager().toExecutionList(this.getAnnotations()).forEach(this.executeClass());
    }

    default void transform() {
        getBootstrapper().getAnnotationManager().toExecutionList(this.getAnnotations())
                .stream()
                .filter(getBootstrapper().getAnnotationManager()::isWinry)
                .map(p -> (IWinryPreparedAnnotationProcessor) p)
                .forEach(this.transformClass());
    }

    Consumer<IPreparedAnnotationProcessor> executeClass();

    Consumer<IWinryPreparedAnnotationProcessor> transformClass();

    default int compareTo(IBootstrappedElement<?> value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}