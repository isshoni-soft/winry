package tv.isshoni.winry.api.annotation.processor;

import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.model.bytebuddy.FieldTransformingPlan;
import tv.isshoni.winry.internal.model.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.model.bytebuddy.MethodTransformingPlan;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface IWinryAnnotationProcessor<A extends Annotation> extends IAnnotationProcessor<A>, IContextual {

    Function<AraragiLogger, Runnable> NO_WINRY_METHOD_TRANSFORMER = logger -> () -> logger.error("Could not register method delegator to non WinryMethodTransformer");

    @Deprecated
    default void transformClass(BootstrappedClass bootstrappedClass, ITransformingBlueprint blueprint, A annotation) { }

    default void transformClass(IAnnotatedClass classMeta, IWrapperGenerator generator, A annotation) { }

    @Deprecated
    default void transformMethod(BootstrappedMethod bootstrappedMethod, MethodTransformingPlan methodPlan, A annotation, ITransformingBlueprint blueprint) { }

    default void transformMethod(IAnnotatedMeta<Method> method, IWrapperGenerator generator, A annotation) { }

    @Deprecated
    default void transformField(BootstrappedField bootstrappedField, FieldTransformingPlan fieldPlan, A annotation, ITransformingBlueprint blueprint) { }

    default void transformField(IAnnotatedField field, IWrapperGenerator generator, A annotation) { }

    @Deprecated
    default void executeClass(BootstrappedClass clazz, A annotation) { }

    default void executeClass(IAnnotatedClass clazz, A annotation) { }

    @Deprecated
    default void executeField(BootstrappedField field, A annotation) { }

    default void executeField(IAnnotatedField field, Object target, A annotation) { }

    @Deprecated
    default void executeMethod(BootstrappedMethod method, A annotation) { }

    default void executeMethod(IAnnotatedMeta<Method> method, A annotation) { }

    @Deprecated
    default void executeClass(Class<?> clazz, A annotation) {
        this.executeClass(this.getWinryContext().getElementBootstrapper().getBootstrappedClass(clazz), annotation);
    }

    default void executeField(Field field, Object target, A annotation) {
        this.executeField(this.getWinryContext().getMetaManager().getMeta(field.getDeclaringClass())
                .getField(field), target, annotation);
    }

    @Deprecated
    default void executeMethod(Method method, A annotation) {
        this.executeMethod(this.getWinryContext().getElementBootstrapper().getBootstrappedMethod(method), annotation);
    }

    default void executeClass(Object obj, Class<?> clazz, A annotation) {
        this.executeClass(clazz, annotation);
    }

    default void executeField(Object obj, Field field, A annotation) {
        this.executeField(field, obj, annotation);
    }

    default void executeMethod(Object obj, Method method, A annotation) {
        this.executeMethod(method, annotation);
    }
}
