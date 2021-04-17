package tv.isshoni.winry.bootstrap;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.bootstrap.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.IBootstrappedElement;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleBootstrapper implements IBootstrapper {

    private static final List<Class<? extends Annotation>> bootstrappableAnnotations = new LinkedList<>() {{
        add(Bootstrap.class);
        add(Inject.class);
        add(Injected.class);
        add(Logger.class);
        add(Runner.class);
    }};

    private static final WinryLogger LOGGER = WinryLogger.create("SimpleBootstrapper");

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        LOGGER.info("Beginning bootstrap process...");
        LOGGER.info("Bootstrapper class discovery...");
        Set<Class<?>> clazzes = this.discoverClasses(bootstrap, clazz);
        clazzes.addAll(provided.keySet());
        LOGGER.info("Bootstrapper discovered " + clazzes.size() + " classes");
        LOGGER.info("Introducing provided classes...");
        LOGGER.info("Preparing classes...");
        List<IBootstrappedElement<?, ?>> finalizedElements = this.finalizeClasses(bootstrap, this.prepareClasses(bootstrap, clazzes), provided);
        LOGGER.info("Finished class discovery and instantiation...");
        LOGGER.info("Boot order:");
        LOGGER.setIndent(4);
        finalizedElements.forEach(e -> LOGGER.info(e.toString()));
        LOGGER.setIndent(0);
        LOGGER.info("Executing:");
        LOGGER.setIndent(4);
        finalizedElements.forEach(e -> {
            LOGGER.info("Executing: " + e.toString());

            e.execute(provided);
        });
        LOGGER.setIndent(0);
    }

    @Override
    public List<IBootstrappedElement<?, ?>> finalizeClasses(Bootstrap bootstrap, Map<Class<?>, BootstrappedClass<?>> clazzes, Map<Class<?>, Object> provided) {
        LOGGER.info("Finalizing classes...");
        clazzes.values().forEach(c -> {
            LOGGER.info("Finalizing: " + c.getBootstrappedElement().getName());
            LOGGER.setIndent(4);

            c.addField(Arrays.stream(c.getBootstrappedElement().getDeclaredFields())
                    .filter(f -> getOurAnnotation(f) != null)
                    .map(f -> new BootstrappedField<>(f, getOurAnnotation(f),  clazzes.get(f.getType())))
                    .collect(Collectors.toSet()));
            LOGGER.info("Discovered " + c.getFields().size() + " fields");

            c.addMethod(Arrays.stream(c.getBootstrappedElement().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Runner.class))
                    .map(m -> new BootstrappedMethod(m, m.getAnnotation(Runner.class)))
                    .collect(Collectors.toSet()));
            LOGGER.info("Discovered " + c.getMethods().size() + " methods");
            LOGGER.setIndent(0);
        });

        // TECHNICAL DEBT: This feels like it can be handled better than with just two streams
        List<IBootstrappedElement<?, ?>> result = new LinkedList<>(clazzes.values());

        result.addAll(clazzes.values().stream()
                .flatMap(b -> b.getMethods().stream())
                .collect(Collectors.toList()));

        result.addAll(clazzes.values().stream()
                .flatMap(b -> b.getFields().stream())
                .collect(Collectors.toList()));

        Collections.sort(result);

        return result;
    }

    @Override
    public Set<Class<?>> discoverClasses(Bootstrap bootstrap, Class<?> baseClazz) {
        LOGGER.info("Beginning class discovery process...");

        Set<Class<?>> clazzes = new HashSet<>();
        clazzes.add(baseClazz);
        clazzes.addAll(Arrays.asList(bootstrap.manualLoad()));

        LOGGER.info("Discovered manually loaded classes: " + Arrays.toString(bootstrap.manualLoad()));
        LOGGER.info("Discovering classes from packages: " + Arrays.toString(bootstrap.loadPackage()));

        if (bootstrap.loadPackage().length > 0) {
            String[] packages = bootstrap.loadPackage();

            FilterBuilder filter = new FilterBuilder().includePackage(packages);

            for (Class<?> clazz : bootstrap.manualLoad()) {
                filter.includePackage(clazz);
            }

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false), new ResourcesScanner())
                    .forPackages(packages)
                    .filterInputsBy(filter));

            clazzes.addAll(reflections.getTypesAnnotatedWith(Injected.class));
        }

        return clazzes;
    }

    @Override
    public Map<Class<?>, BootstrappedClass<?>> prepareClasses(Bootstrap bootstrap, Set<Class<?>> clazzes) {
        Map<Class<?>, BootstrappedClass<?>> result = new HashMap<>();

        clazzes.forEach(c -> {
            Annotation annotation = getOurAnnotation(c);

            if (Objects.isNull(annotation)) {
                return; // No need to keep processing here
            }

            result.put(c, new BootstrappedClass<>(c, annotation));
        });

        return result;
    }

    private Annotation getOurAnnotation(AnnotatedElement element) {
        Annotation result = element.getAnnotation(Bootstrap.class);

        if (result != null) {
            return result;
        }

        result = element.getAnnotation(Logger.class);

        if (result != null) {
            return result;
        }

        return element.getAnnotation(Injected.class);
    }
}
