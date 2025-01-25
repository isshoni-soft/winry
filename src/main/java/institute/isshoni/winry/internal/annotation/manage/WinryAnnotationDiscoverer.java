package institute.isshoni.winry.internal.annotation.manage;

import institute.isshoni.araragi.annotation.Depends;
import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import institute.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.araragi.util.ComparatorUtil;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Injected;
import institute.isshoni.winry.api.annotation.meta.BeforeInjections;
import institute.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import institute.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class WinryAnnotationDiscoverer extends SimpleAnnotationDiscoverer {

    private final AraragiLogger LOGGER;

    public WinryAnnotationDiscoverer(IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        super(annotationManager);

        this.LOGGER = loggerFactory.createLogger(this.getClass());
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return (IWinryAnnotationManager) this.annotationManager;
    }

    @Override
    public IAnnotationDiscoverer discoverAnnotations() {
        Set<Class<? extends Annotation>> all = findProcessorAnnotations();
        Set<Class<? extends Annotation>> beforeInjections = all.stream()
                .filter(e -> e.isAnnotationPresent(BeforeInjections.class))
                .collect(Collectors.toSet());
        List<Class<? extends Annotation>> ordered = new LinkedList<>(all);
        ordered.sort((first, second) -> {
            int simple = ComparatorUtil.simpleCompare(first, second, e -> e.isAnnotationPresent(BeforeInjections.class));

            if (simple != 2) {
                return simple;
            }

            return -1;
        });
        LOGGER.debug("Sorted discovered annotations: ${0}", ordered.toString());

        ordered.forEach(ac -> safelyRecursiveDiscover(ac, all, beforeInjections, new Stack<>()));

        return this;
    }

    @Override
    public <A extends Annotation> Set<Class<?>> findWithAnnotations(Class<A> clazz) {
        Set<Class<?>> result = new HashSet<>(super.findWithAnnotations(clazz));

        for (Class<?> manLoad : getAnnotationManager().getAllManuallyLoaded()) {
            if (!manLoad.isAnnotationPresent(clazz)) {
                continue;
            }

            result.add(manLoad);
        }

        Class<?> bootstrapped = getAnnotationManager().getBootstrapper().getBootstrapped();

        if (bootstrapped.isAnnotationPresent(clazz)) {
            result.add(bootstrapped);
        }

        return result;
    }

    public void safelyRecursiveDiscover(Class<? extends Annotation> clazz, Set<Class<? extends Annotation>> all,
                                        Set<Class<? extends Annotation>> beforeInjections,
                                        Stack<Class<? extends Annotation>> levels) {
        beforeInjections = beforeInjections.stream().filter(c -> !c.equals(clazz)).collect(Collectors.toSet());

        if (getAnnotationManager().isManagedAnnotation(clazz)) { // already discovered
            return;
        }

        LOGGER.debug("Discovering: ${0}", clazz.getSimpleName());

        Processor processor = clazz.getAnnotation(Processor.class);

        Set<Class<? extends Annotation>> annotations = Streams.to(processor.value())
                .map(getAnnotationManager()::getAllAnnotationsIn)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        if (clazz.isAnnotationPresent(Depends.class)) {
            annotations.addAll(getAnnotationManager().getAllAnnotationsIn(clazz));
        }

        if (!clazz.equals(Injected.class)) {
            if (Arrays.asList(processor.value()).contains(BootstrapClassProcessor.class)) {
                for (Class<?> c : findWithAnnotations(clazz)) {
                    Set<Class<? extends Annotation>> construct = annotationManager.getAllAnnotationsForConstruction(c);

                    if (construct.contains(Inject.class)) {
                        annotations.add(Injected.class);
                    }
                }
            }

            if (annotations.contains(Inject.class)) {
                annotations.add(Injected.class);
            }
        }

        if (annotations.isEmpty() || annotations.stream()
                .allMatch(getAnnotationManager()::isManagedAnnotation)) {
            getAnnotationManager().discoverAnnotation(clazz);
            return;
        }

        List<Class<? extends Annotation>> unregistered = new LinkedList<>();

        if (annotations.contains(Injected.class)) {
            unregistered.addAll(beforeInjections);
            unregistered.addAll(annotations);
        } else {
            unregistered.addAll(annotations);
        }

        unregistered = Streams.to(unregistered)
                .filterInverted(getAnnotationManager()::isManagedAnnotation)
                .toList();

        for (Class<? extends Annotation> uclazz : unregistered) {
            if (clazz.equals(uclazz) || levels.contains(uclazz)) {
                throw new IllegalStateException("Found circular dependency; Levels: " + levels + " Current: " + clazz + " UClass: " + uclazz);
            }

            if (all.contains(uclazz)) {
                levels.add(clazz);
                safelyRecursiveDiscover(uclazz, all, beforeInjections, levels);
                levels.pop();
            }
        }

        getAnnotationManager().discoverAnnotation(clazz);
    }

    @Override
    public void safelyRecursiveDiscover(Class<? extends Annotation> clazz, Set<Class<? extends Annotation>> all, Stack<Class<? extends Annotation>> levels) {
        safelyRecursiveDiscover(clazz, all, new HashSet<>(), levels);
    }
}
