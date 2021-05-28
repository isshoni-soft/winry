package tv.isshoni.winry.annotation.manage;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.processor.BasicClassProcessor;
import tv.isshoni.winry.entity.annotation.AnnotationProcessor;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationManager {

    private final Map<Class<? extends Annotation>, List<AnnotationProcessor<?>>> annotationProcessors;

    public AnnotationManager() {
        this.annotationProcessors = new HashMap<>();

        register(Bootstrap.class, new BasicClassProcessor()); // This is one of the few hard codes i will enforce
    }

    public final <T extends Annotation> void register(Class<T> annotation, AnnotationProcessor<?>... processor) {
        annotationProcessors.compute(annotation, (a, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.addAll(Arrays.asList(processor));

            return v;
        });
    }

    public <A extends Annotation> int calculateWeight(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .mapToInt(p -> p.getSecond().getWeight(p.getFirst()))
                .sum();
    }

    public List<AnnotationProcessor<?>> get(Class<? extends Annotation> annotation) {
        return this.annotationProcessors.getOrDefault(annotation, new LinkedList<>());
    }

    public Collection<Class<? extends Annotation>> getManagedAnnotations() {
        return this.annotationProcessors.keySet();
    }

    public List<PreparedAnnotationProcessor> toExecutionList(Collection<Annotation> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .map(p -> new PreparedAnnotationProcessor(p.getFirst(), p.getSecond()))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Annotation> getManagedAnnotationsOn(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .filter(f -> this.annotationProcessors.containsKey(f.annotationType()))
                .collect(Collectors.toList());
    }

    public List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> getConflictingAnnotations(Collection<Annotation> annotations) {
        List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> result = new LinkedList<>();

        List<Class<? extends Annotation>> annotationTypes = annotations.stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toList());

        convertCollectionToProcessorStream(annotations)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
                .forEach((a, p) -> result.addAll(p.getIncompatibleWith(a).stream()
                        .filter(annotationTypes::contains)
                        .map(t -> new Pair<Class<? extends Annotation>, Class<? extends Annotation>>(a.annotationType(), t))
                        .collect(Collectors.toList())));

        return result;
    }

    public boolean hasManagedAnnotation(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(this.annotationProcessors::containsKey);
    }

    public <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .anyMatch(p -> p.getSecond().getIncompatibleWith(p.getFirst())
                        .stream()
                        .anyMatch(c -> annotations.stream().map(Annotation::annotationType).anyMatch(c::equals)));
    }

    private <A extends Annotation> Stream<Pair<A, AnnotationProcessor<A>>> convertCollectionToProcessorStream(Collection<A> annotations) {
        return annotations.stream()
                .flatMap(a -> get(a.annotationType()).stream()
                        .map(p -> new Pair<>(a, (AnnotationProcessor<A>) p)));
    }
}
