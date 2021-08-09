package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.internal.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;

import java.lang.annotation.Annotation;

public class WinryPreparedAnnotationProcessor extends PreparedAnnotationProcessor implements IWinryPreparedAnnotationProcessor {

    public WinryPreparedAnnotationProcessor(Annotation annotation, IWinryAnnotationProcessor<Annotation> processor) {
        super(annotation, processor);
    }

    public IWinryAnnotationProcessor<Annotation> asWinry() {
        return (IWinryAnnotationProcessor<Annotation>) this.processor;
    }

    @Override
    public void transformClass(BootstrappedClass bootstrappedClass, ClassTransformingBlueprint blueprint) {
        asWinry().transformClass(bootstrappedClass, blueprint, this.annotation);
    }

    @Override
    public void transformMethod(BootstrappedMethod bootstrappedMethod, ClassTransformingBlueprint blueprint) {
        asWinry().transformMethod(bootstrappedMethod, blueprint, this.annotation);
    }

    @Override
    public void transformField(BootstrappedField bootstrappedField, ClassTransformingBlueprint blueprint) {
        asWinry().transformField(bootstrappedField, blueprint, this.annotation);
    }
}
