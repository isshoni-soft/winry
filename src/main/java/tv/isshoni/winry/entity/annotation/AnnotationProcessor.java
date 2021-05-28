package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.annotation.manage.WeightCalculator;
import tv.isshoni.winry.entity.element.BootstrappedClass;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.entity.element.BootstrappedMethod;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface AnnotationProcessor<A extends Annotation> {

    WeightCalculator WEIGHT_CALCULATOR = new WeightCalculator();

    default void onClass(BootstrappedClass clazz, A annotation, Map<Class<?>, Object> provided) { }

    default void onField(BootstrappedField field, A annotation, Map<Class<?>, Object> provided) { }

    default void onMethod(BootstrappedMethod method, A annotation, Map<Class<?>, Object> provided) { }

    default int getWeight(A annotation) {
        return WEIGHT_CALCULATOR.calculateWeight(annotation);
    }

    default List<Class<? extends Annotation>> getIncompatibleWith(A annotation) {
        return new LinkedList<>();
    }
}
