package tv.isshoni.winry.bootstrap;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.entity.bootstrap.BootstrappedClass;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleBootstrapper implements IBootstrapper {

    private static final Function<Class<?>, BiFunction<BootstrappedClass.Type, List<Class<?>>, List<Class<?>>>> ORGANIZE_MAP_COMPUTE = (c) -> (k, v) -> {
        if (v == null) {
            return new LinkedList<>() {{
                add(c);
            }};
        }

        v.add(c);
        return v;
    };

    private static final WinryLogger LOGGER = WinryLogger.create("SimpleBootstrapper");

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Object[] provided) {
        LOGGER.info("Beginning bootstrap process...");
        LOGGER.info("Bootstrapper class discovery...");
        Set<Class<?>> clazzes = this.discoverClasses(bootstrap, clazz);
        LOGGER.info("Bootstrapper discovered " + clazzes.size() + " classes");
        LOGGER.info("Preparing classes...");
        Set<BootstrappedClass<?>> preparedClasses = this.prepareClasses(bootstrap, clazzes);
        LOGGER.info("Finalizing classes...");
        this.finalizeClasses(bootstrap, preparedClasses);
    }

    @Override
    public void finalizeClasses(Bootstrap bootstrap, Set<BootstrappedClass<?>> clazzes) {
        clazzes.forEach(c -> {
            // TODO: Iterate on class set and discover fields and methods then insert into BootstrappedClass object.
        });
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
    public Set<BootstrappedClass<?>> prepareClasses(Bootstrap bootstrap, Set<Class<?>> clazzes) {
        Set<BootstrappedClass<?>> result = new HashSet<>();

        clazzes.forEach(c -> {
            Annotation annotation = getOurAnnotation(c);

            if (Objects.isNull(annotation)) {
                return; // No need to keep processing here
            }

            if (annotation instanceof Bootstrap) {
                result.add(new BootstrappedClass<>(c, (Bootstrap) annotation, BootstrappedClass.Type.BOOTSTRAP_CLASS));
            } else if (annotation instanceof Injected) {
                Injected injected = (Injected) annotation;

                BootstrappedClass.Type type = null;
                switch (injected.value()) {
                    case DEFAULT:
                        type = BootstrappedClass.Type.INJECTED_DEFAULT;
                        break;
                    case SERVICE:
                        type = BootstrappedClass.Type.INJECTED_SERVICE;
                        break;
                    case DATABASE:
                        type = BootstrappedClass.Type.INJECTED_DATABASE;
                        break;
                }

                result.add(new BootstrappedClass<>(c, (Injected) annotation, type));
            }
        });

        return result;
    }

    private Annotation getOurAnnotation(Class<?> clazz) {
        Annotation result = clazz.getAnnotation(Bootstrap.class);

        if (result != null) {
            return result;
        }

        return clazz.getAnnotation(Injected.class);
    }
}
