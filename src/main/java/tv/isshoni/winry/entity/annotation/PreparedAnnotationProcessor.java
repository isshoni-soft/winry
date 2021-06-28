package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.annotation.Annotation;

public class PreparedAnnotationProcessor implements Comparable<PreparedAnnotationProcessor> {

    private final Annotation annotation;

    private final IAnnotationProcessor<Annotation> processor;

    public PreparedAnnotationProcessor(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        this.annotation = annotation;
        this.processor = processor;
    }

    public void transformClass(BootstrappedClass bootstrappedClass, ClassTransformingBlueprint blueprint) {
        this.processor.transformClass(bootstrappedClass, blueprint, this.annotation);
    }

    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint) {
        this.processor.transformMethod(bootstrappedMethod, blueprint, this.annotation);
    }

    public void transformField(BootstrappedField bootstrappedField, ClassTransformingBlueprint blueprint) {
        this.processor.transformField(bootstrappedField, blueprint, this.annotation);
    }

    public void executeClass(BootstrappedClass bootstrappedClass) {
        this.processor.executeClass(bootstrappedClass, this.annotation);
    }

    public void executeMethod(BootstrappedMethod bootstrappedMethod) {
        this.processor.executeMethod(bootstrappedMethod, this.annotation);
    }

    public void executeField(BootstrappedField bootstrappedField) {
        this.processor.executeField(bootstrappedField, this.annotation);
    }

    @Override
    public int compareTo(PreparedAnnotationProcessor o) {
        return Integer.compare(this.processor.getWeight(this.annotation), o.processor.getWeight(o.annotation));
    }
}
