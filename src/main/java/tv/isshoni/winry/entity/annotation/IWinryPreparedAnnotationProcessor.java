package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.ITransformingBlueprint;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAnnotationProcessor extends IPreparedAnnotationProcessor<IWinryAnnotationProcessor<Annotation>> {

    default void transformClass(BootstrappedClass bootstrappedClass, ITransformingBlueprint blueprint) {
        this.getProcessor().transformClass(bootstrappedClass, blueprint, this.getAnnotation());
    }

    default void transformMethod(BootstrappedMethod bootstrappedMethod, ITransformingBlueprint blueprint) {
        this.getProcessor().transformMethod(bootstrappedMethod, blueprint.getMethodTransformingPlan(bootstrappedMethod.getBootstrappedElement()), this.getAnnotation(), blueprint);
    }

    default void transformField(BootstrappedField bootstrappedField, ITransformingBlueprint blueprint) {
        this.getProcessor().transformField(bootstrappedField, blueprint.getFieldTransformingPlan(bootstrappedField.getBootstrappedElement()), this.getAnnotation(), blueprint);
    }
}
