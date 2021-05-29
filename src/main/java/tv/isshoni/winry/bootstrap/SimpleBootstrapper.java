package tv.isshoni.winry.bootstrap;

import org.reflections8.Reflections;
import org.reflections8.scanners.ResourcesScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.bytebuddy.ByteBuddyUtil;
import tv.isshoni.winry.entity.element.BootstrappedClass;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.entity.element.BootstrappedMethod;
import tv.isshoni.winry.entity.element.IBootstrappedElement;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleBootstrapper implements IBootstrapper {

    private static final WinryLogger LOGGER = WinryLogger.create("SimpleBootstrapper");

    private final AnnotationManager annotationManager;

    // TODO: make reflection manager less of a singleton
    private final ReflectionManager reflectionManager;

    public SimpleBootstrapper() {
        this.annotationManager = new AnnotationManager();
        this.reflectionManager = new ReflectionManager();
    }

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        LOGGER.info("Beginning bootstrap process...");
        LOGGER.info("Bootstrapper class discovery...");
        Set<Class<?>> clazzes = this.discoverClasses(bootstrap, clazz);
        clazzes.addAll(provided.keySet());
        LOGGER.info("Bootstrapper discovered " + clazzes.size() + " classes");
        LOGGER.info("Preparing classes...");
        List<IBootstrappedElement<?>> finalizedElements = this.finalizeClasses(bootstrap, this.prepareClasses(bootstrap, clazzes), provided);
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
    public List<IBootstrappedElement<?>> finalizeClasses(Bootstrap bootstrap, Map<Class<?>, BootstrappedClass> clazzes, Map<Class<?>, Object> provided) {
        LOGGER.info("Finalizing classes...");
        clazzes.values().forEach(c -> {
            LOGGER.info("Finalizing: " + c.getBootstrappedElement().getName());
            LOGGER.setIndent(4);

            if (provided.containsKey(c.getBootstrappedElement())) {
                c.setProvided(true);
                LOGGER.info("Provided Class");
            }

            c.addField(Arrays.stream(c.getBootstrappedElement().getDeclaredFields())
                    .filter(this.annotationManager::hasManagedAnnotation)
                    .map(f -> new BootstrappedField(f, clazzes.get(f.getType()), this.annotationManager))
                    .collect(Collectors.toSet()));
            LOGGER.info("Discovered " + c.getFields().size() + " fields");

            c.addMethod(Arrays.stream(c.getBootstrappedElement().getDeclaredMethods())
                    .filter(this.annotationManager::hasManagedAnnotation)
                    .map(m -> new BootstrappedMethod(m, this.annotationManager))
                    .collect(Collectors.toSet()));
            LOGGER.info("Discovered " + c.getMethods().size() + " methods");
            LOGGER.info("Wrapping class...");
            c.setWrappedClass(ByteBuddyUtil.wrapClass(c)
                    .name("WinryWrapped" + c.getBootstrappedElement().getSimpleName())
                    .make()
                    .load(ClassLoader.getSystemClassLoader())
                    .getLoaded());
            LOGGER.setIndent(0);
        });

        // TECHNICAL DEBT: This feels like it can be handled better than with just two streams
        List<IBootstrappedElement<?>> result = new LinkedList<>(clazzes.values());

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

            this.annotationManager.getManagedAnnotations().forEach(a -> clazzes.addAll(reflections.getTypesAnnotatedWith(a)));
        }

        return clazzes;
    }

    @Override
    public Map<Class<?>, BootstrappedClass> prepareClasses(Bootstrap bootstrap, Set<Class<?>> clazzes) {
        Map<Class<?>, BootstrappedClass> result = new HashMap<>();

        clazzes.forEach(c -> {
            if (!this.annotationManager.hasManagedAnnotation(c)) {
                return; // No need to keep processing here
            }

            result.put(c, new BootstrappedClass(c, this.annotationManager));
        });

        return result;
    }
}
