package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.entity.annotation.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BootstrapAnnotationManager {

    private static final Map<Class<? extends Annotation>, List<AnnotationProcessor>> ANNOTATION_PROCESSORS = new HashMap<>();

    public static void register(Class<? extends Annotation> annotation, AnnotationProcessor... processor) {
        ANNOTATION_PROCESSORS.compute(annotation, (a, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.addAll(Arrays.asList(processor));

            return v;
        });
    }

    public static List<AnnotationProcessor> get(Class<? extends Annotation> annotation) {
        return ANNOTATION_PROCESSORS.getOrDefault(annotation, new LinkedList<>());
    }

    public static Collection<Class<? extends Annotation>> getBootstrapAnnotations() {
        return ANNOTATION_PROCESSORS.keySet();
    }

    public static List<Annotation> getBootstrapAnnotationsOn(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .filter(f -> ANNOTATION_PROCESSORS.containsKey(f.getClass()))
                .collect(Collectors.toList());
    }

    public static boolean hasBootstrapAnnotation(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .map(Annotation::getClass)
                .anyMatch(ANNOTATION_PROCESSORS::containsKey);
    }
}
