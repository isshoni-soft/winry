package tv.isshoni.winry.internal.model.annotation.prepare;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.model.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAnnotationProcessor<AP extends IWinryAnnotationProcessor<Annotation>> extends IPreparedAnnotationProcessor<AP> {

    @Deprecated
    default void transformClass(BootstrappedClass bootstrappedClass, ITransformingBlueprint blueprint) {
        this.getProcessor().transformClass(bootstrappedClass, blueprint, this.getAnnotation());
    }

    default void transformClass(IAnnotatedClass classMeta, IWrapperGenerator generator) {
        this.getProcessor().transformClass(classMeta, generator, this.getAnnotation());
    }

    @Deprecated
    default void transformMethod(BootstrappedMethod bootstrappedMethod, ITransformingBlueprint blueprint) {
        this.getProcessor().transformMethod(bootstrappedMethod, blueprint.getMethodTransformingPlan(bootstrappedMethod.getBootstrappedElement()), this.getAnnotation(), blueprint);
    }

    @Deprecated
    default void transformField(BootstrappedField bootstrappedField, ITransformingBlueprint blueprint) {
        this.getProcessor().transformField(bootstrappedField, blueprint.getFieldTransformingPlan(bootstrappedField.getBootstrappedElement()), this.getAnnotation(), blueprint);
    }
}
