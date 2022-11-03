package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.util.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public interface IBootstrappedElement<E extends AnnotatedElement> extends IExecutable {

    List<Annotation> getAnnotations();

    E getBootstrappedElement();

    Set<ReflectedModifier> getModifiers();

    IBootstrapper getBootstrapper();

    String getSimpleName();

    default String getDisplay() {
        return getSimpleName() + ": " + getBootstrappedElement().toString() + " [" + Streams.to(getAnnotations()).collapse((Annotation a, String s) -> {
            String result = (Objects.isNull(s) ? "" : s);

            if (result.length() > 0) {
                result += ", ";
            }

            return result + a.annotationType().getSimpleName();
        }) + "] (" + getWeight() + ")";
    }

    default int getWeight() {
        return this.getBootstrapper().getContext().getAnnotationManager().calculateWeight(this.getAnnotations());
    }

    default void execute() {
        getBootstrapper().getContext().getAnnotationManager().toExecutionList(new Pair<>(this.getBootstrappedElement(), this.getAnnotations()))
                .forEach(this.executeClass());
    }

    default void transform() {
        getBootstrapper().getContext().getAnnotationManager().toExecutionList(new Pair<>(this.getBootstrappedElement(), this.getAnnotations()))
                .stream()
                .filter(getBootstrapper().getContext().getAnnotationManager()::isWinry)
                .map(p -> (IWinryPreparedAnnotationProcessor) p)
                .forEach(this.transformClass());
    }

    Consumer<IPreparedAnnotationProcessor> executeClass();

    Consumer<IWinryPreparedAnnotationProcessor> transformClass();
}
