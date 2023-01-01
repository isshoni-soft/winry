package tv.isshoni.winry.api.annotation.processor;

import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface IWinryAnnotationProcessor<A extends Annotation> extends IAnnotationProcessor<A>, IContextual {

    Function<AraragiLogger, Runnable> NO_WINRY_METHOD_TRANSFORMER = logger -> () -> logger.error("Could not register method delegator to non WinryMethodTransformer");

    default void transformClass(IAnnotatedClass classMeta, IWrapperGenerator generator, A annotation) { }

    default void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator, A annotation) { }

    default void transformField(IAnnotatedField field, IWrapperGenerator generator, A annotation) { }

    default void executeClass(IAnnotatedClass clazz, Object target, A annotation) { }

    default void executeField(IAnnotatedField field, Object target, A annotation) { }

    default void executeMethod(IAnnotatedMethod method, Object target, A annotation) { }

    default void executeClass(Class<?> clazz, Object target, A annotation) {
        this.executeClass(this.getWinryContext().getMetaManager().getMeta(clazz), target, annotation);
    }

    default void executeField(Field field, Object target, A annotation) {
        this.executeField(this.getWinryContext().getMetaManager().getMeta(field.getDeclaringClass())
                .getField(field), target, annotation);
    }

    default void executeMethod(Method method, Object target, A annotation) {
        this.executeMethod(this.getWinryContext().getMetaManager().getMeta(method.getDeclaringClass())
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
