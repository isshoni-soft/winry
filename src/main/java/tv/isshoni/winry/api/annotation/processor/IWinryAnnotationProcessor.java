package tv.isshoni.winry.api.annotation.processor;

import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.FieldTransformingPlan;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface IWinryAnnotationProcessor<A extends Annotation> extends IAnnotationProcessor<A>, IContextual {

    Function<AraragiLogger, Runnable> NO_WINRY_METHOD_TRANSFORMER = logger -> () -> logger.error("Could not register method delegator to non WinryMethodTransformer");

    default void transformClass(BootstrappedClass bootstrappedClass, ITransformingBlueprint blueprint, A annotation) { }

    default void transformMethod(BootstrappedMethod bootstrappedMethod, MethodTransformingPlan methodPlan, A annotation, ITransformingBlueprint blueprint) { }

    default void transformField(BootstrappedField bootstrappedField, FieldTransformingPlan fieldPlan, A annotation, ITransformingBlueprint blueprint) { }

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

    default void executeClass(Object obj, Class<?> clazz, A annotation) {
        this.executeClass(clazz, annotation);
    }

    default void executeField(Object obj, Field field, A annotation) {
        this.executeField(field, annotation);
    }

    default void executeMethod(Object obj, Method method, A annotation) {
        this.executeMethod(method, annotation);
    }
}
