package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.internal.annotation.manage.WeightCalculator;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

public interface IAnnotationProcessor<A extends Annotation> {

    default void transformClass(BootstrappedClass bootstrappedClass, ClassTransformingBlueprint blueprint, A annotation) { }

    default void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, A annotation) { }

    default void transformField(BootstrappedField bootstrappedField, ClassTransformingBlueprint blueprint, A annotation) { }

    default void executeClass(BootstrappedClass clazz, A annotation) { }

    default void executeField(BootstrappedField field, A annotation) { }

    default void executeMethod(BootstrappedMethod method, A annotation) { }

    default int getWeight(A annotation) {
        return WeightCalculator.INSTANCE.calculateWeight(annotation);
    }

    default List<Class<? extends Annotation>> getIncompatibleWith(A annotation) {
        return new LinkedList<>();
    }
}
