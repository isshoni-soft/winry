package tv.isshoni.winry.internal.model.annotation.prepare;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedField;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAnnotationProcessor<AP extends IWinryAnnotationProcessor<Annotation>> extends IPreparedAnnotationProcessor<AP> {

    default void transformClass(IAnnotatedClass classMeta, IWrapperGenerator generator) {
        this.getProcessor().transformClass(classMeta, generator, this.getAnnotation());
    }

    default void transformMethod(IAnnotatedMethod method, IWrapperGenerator generator) {
        this.getProcessor().transformMethod(method, generator, this.getAnnotation());
    }

    default void transformField(IAnnotatedField field, IWrapperGenerator generator) {
        this.getProcessor().transformField(field, generator, this.getAnnotation());
    }
}
