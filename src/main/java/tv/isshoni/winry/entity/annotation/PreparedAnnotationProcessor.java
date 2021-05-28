package tv.isshoni.winry.entity.annotation;

import tv.isshoni.winry.entity.element.BootstrappedClass;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.entity.element.BootstrappedMethod;

import java.lang.annotation.Annotation;
import java.util.Map;

public class PreparedAnnotationProcessor implements Comparable<PreparedAnnotationProcessor> {

    private final Annotation annotation;

    private final AnnotationProcessor<Annotation> processor;

    public PreparedAnnotationProcessor(Annotation annotation, AnnotationProcessor<Annotation> processor) {
        this.annotation = annotation;
        this.processor = processor;
    }

    public void executeClass(BootstrappedClass bootstrappedClass, Map<Class<?>, Object> provided) {
        this.processor.onClass(bootstrappedClass, this.annotation, provided);
    }

    public void executeMethod(BootstrappedMethod bootstrappedMethod, Map<Class<?>, Object> provided) {
        this.processor.onMethod(bootstrappedMethod, this.annotation, provided);
    }

    public void executeField(BootstrappedField bootstrappedField, Map<Class<?>, Object> provided) {
        this.processor.onField(bootstrappedField, this.annotation, provided);
    }

    @Override
    public int compareTo(PreparedAnnotationProcessor o) {
        return Integer.compare(this.processor.getWeight(this.annotation), o.processor.getWeight(o.annotation));
    }
}
