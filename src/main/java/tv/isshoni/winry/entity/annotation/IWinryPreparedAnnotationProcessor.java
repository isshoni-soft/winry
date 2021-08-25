package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAnnotationProcessor extends IPreparedAnnotationProcessor<IWinryAnnotationProcessor<Annotation>> {

    default void transformClass(BootstrappedClass bootstrappedClass, ClassTransformingBlueprint blueprint) {
        this.getProcessor().transformClass(bootstrappedClass, blueprint, this.getAnnotation());
    }

    default void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint) {
        this.getProcessor().transformMethod(bootstrappedMethod, blueprint, this.getAnnotation());
    }

    default void transformField(BootstrappedField bootstrappedField, ClassTransformingBlueprint blueprint) {
        this.getProcessor().transformField(bootstrappedField, blueprint, this.getAnnotation());
    }
}
