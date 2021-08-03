package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.context.IContextual;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IWinryAnnotationProcessor<A extends Annotation> extends IAnnotationProcessor<A>, IContextual {

    default void transformClass(BootstrappedClass bootstrappedClass, ClassTransformingBlueprint blueprint, A annotation) { }

    default void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint, A annotation) { }

    default void transformField(BootstrappedField bootstrappedField, ClassTransformingBlueprint blueprint, A annotation) { }

    default void executeClass(BootstrappedClass clazz, A annotation) { }

    default void executeField(BootstrappedField field, A annotation) { }

    default void executeMethod(BootstrappedMethod method, A annotation) { }

    default void executeClass(Class<?> clazz, A annotation) {
        this.executeClass(this.getWinryContext().getElementBootstrapper().getBootstrappedClass(clazz), annotation);
    }

    default void executeField(Field field, A annotation) {
        this.executeField(this.getWinryContext().getElementBootstrapper().getBootstrappedField(field), annotation);
    }

    default void executeMethod(Method method, A annotation) {
        this.executeMethod(this.getWinryContext().getElementBootstrapper().getBootstrappedMethod(method), annotation);
    }
}
