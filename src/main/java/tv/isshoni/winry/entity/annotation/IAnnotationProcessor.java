package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.annotation.manage.WeightCalculator;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

public interface IAnnotationProcessor<A extends Annotation> {

    WeightCalculator WEIGHT_CALCULATOR = new WeightCalculator();

    default void executeClass(BootstrappedClass clazz, A annotation) { }

    default void executeField(BootstrappedField field, A annotation) { }

    default void executeMethod(BootstrappedMethod method, A annotation) { }

    default int getWeight(A annotation) {
        return WEIGHT_CALCULATOR.calculateWeight(annotation);
    }

    default List<Class<? extends Annotation>> getIncompatibleWith(A annotation) {
        return new LinkedList<>();
    }
}
