package tv.isshoni.winry.annotation.manage;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import tv.isshoni.winry.Winry;
import tv.isshoni.winry.annotation.AttachTo;
import tv.isshoni.winry.annotation.Processor;
import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.util.Pair;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Add functionality for annotations to effect class wrapping (i.e. before instantiation & execution)
public class AnnotationManager {

    private static final WinryLogger LOGGER = WinryLogger.create("AnnotationManager");

    private final Map<Class<? extends Annotation>, List<IAnnotationProcessor<?>>> annotationProcessors;

    public AnnotationManager() {
        LOGGER.info("Initializing...");
        this.annotationProcessors = new HashMap<>();

        ArrayList<String> packages = new ArrayList<>(Arrays.asList(Winry.getPackages()));
        packages.add("tv.isshoni.winry.annotation");

        LOGGER.info("Performing annotation discovery...");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
                .forPackages(packages.toArray(new String[0])));

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Processor.class);

        classes.stream()
                .map(c -> (Class<? extends Annotation>) c)
                .filter(c -> c.isAnnotationPresent(Processor.class))
                .forEach(c -> register(c, c.getAnnotation(Processor.class).value()));

        classes = reflections.getTypesAnnotatedWith(AttachTo.class);

        classes.stream()
                .filter(c -> c.isAnnotationPresent(AttachTo.class))
                .filter(IAnnotationProcessor.class::isAssignableFrom)
                .map(c -> (Class<IAnnotationProcessor<?>>) c)
                .forEach(c -> register(c.getAnnotation(AttachTo.class).value(), c));

        LOGGER.info("Discovered " + getTotalProcessors() + " annotation processors.");
    }

    public final <T extends Annotation> void unregister(Class<T> annotation) {
        this.annotationProcessors.remove(annotation);
    }

    @SafeVarargs
    public final void register(Class<? extends Annotation>[] annotations, Class<? extends IAnnotationProcessor<?>>... processors) {
        for (Class<? extends Annotation> annotation : annotations) {
            register(annotation, processors);
        }
    }

    public final void register(Class<? extends Annotation>[] annotations, IAnnotationProcessor<?>... processors) {
        for (Class<? extends Annotation> annotation : annotations) {
            register(annotation, processors);
        }
    }

    @SafeVarargs
    public final void register(Class<? extends Annotation> annotation, Class<? extends IAnnotationProcessor<?>>... processors) {
        register(annotation, Arrays.stream(processors)
                .map(ReflectionManager::construct)
                .toArray(IAnnotationProcessor<?>[]::new));
    }

    public final <T extends Annotation> void register(Class<T> annotation, IAnnotationProcessor<?>... processors) {
        this.annotationProcessors.compute(annotation, (a, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.addAll(Arrays.asList(processors));

            return v;
        });
    }

    public <A extends Annotation> int calculateWeight(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .mapToInt(p -> p.getSecond().getWeight(p.getFirst()))
                .sum();
    }

    public List<IAnnotationProcessor<?>> get(Class<? extends Annotation> annotation) {
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

    public int getTotalProcessors() {
        return this.annotationProcessors.values().stream()
                .mapToInt(Collection::size)
                .sum();
    }

    private <A extends Annotation> Stream<Pair<A, IAnnotationProcessor<A>>> convertCollectionToProcessorStream(Collection<A> annotations) {
        return annotations.stream()
                .flatMap(a -> get(a.annotationType()).stream()
                        .map(p -> new Pair<>(a, (IAnnotationProcessor<A>) p)));
    }
}
