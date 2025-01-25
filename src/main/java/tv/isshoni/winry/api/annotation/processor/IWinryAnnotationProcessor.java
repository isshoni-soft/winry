package tv.isshoni.winry.api.annotation.processor;

import institute.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedField;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IWinryAnnotationProcessor<A extends Annotation> extends IAnnotationProcessor<A>, IContextual {

    default void transformClass(IAnnotatedClass classMeta, IWrapperGenerator generator, A annotation) { }

    default void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator, A annotation) { }

    default void transformField(IAnnotatedField field, IWrapperGenerator generator, A annotation) { }

    default void executeClass(IAnnotatedClass clazz, Object target, A annotation) { }

    default void executeField(IAnnotatedField field, Object target, A annotation) { }

    default void executeMethod(IAnnotatedMethod method, Object target, A annotation) { }

    default void executeClass(Class<?> clazz, Object target, A annotation) {
        this.executeClass(this.getContext().get().getMetaManager().findMeta(target), target, annotation);
    }

    default void executeField(Field field, Object target, A annotation) {
        this.executeField(this.getContext().get().getMetaManager().findMeta(target)
                .getField(field), target, annotation);
    }

    default void executeMethod(Method method, Object target, A annotation) {
        this.executeMethod(this.getContext().get().getMetaManager().findMeta(target)
                .getMethod(method), target, annotation);
    }

    default void executeClass(Object obj, Class<?> clazz, A annotation) {
        this.executeClass(clazz, obj, annotation);
    }

    default void executeField(Object obj, Field field, A annotation) {
        this.executeField(field, obj, annotation);
    }

    default void executeMethod(Object obj, Method method, A annotation) {
        this.executeMethod(method, obj, annotation);
    }
}
