package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.Depends;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.araragi.util.ComparatorUtil;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.annotation.processor.type.BootstrapClassProcessor;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class WinryAnnotationDiscoverer extends SimpleAnnotationDiscoverer implements IContextual {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public WinryAnnotationDiscoverer(IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        super(annotationManager);

        this.context = new Constant<>();
        this.LOGGER = loggerFactory.createLogger(this.getClass());
    }

    public void setContext(IWinryContext context) {
        this.context.set(context);
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return (IWinryAnnotationManager) this.annotationManager;
    }

    @Override
    public IAnnotationDiscoverer discoverAnnotations() {
        Set<Class<? extends Annotation>> all = findProcessorAnnotations();
        List<Class<? extends Annotation>> ordered = new LinkedList<>(all);
        ordered.sort((first, second) -> {
            int simple = ComparatorUtil.simpleCompare(first, second, e -> e.isAnnotationPresent(Transformer.class));

            if (simple != 2) {
                return simple;
            }

            return -1;
        });

        ordered.forEach(ac -> safelyRecursiveDiscover(ac, all, new Stack<>()));

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

    @Override
    public void safelyRecursiveDiscover(Class<? extends Annotation> clazz, Set<Class<? extends Annotation>> all, Stack<Class<? extends Annotation>> levels) {
        if (getAnnotationManager().isManagedAnnotation(clazz)) {
            return;
        }

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

        List<Class<? extends Annotation>> unregistered = Streams.to(annotations)
                .filterInverted(getAnnotationManager()::isManagedAnnotation)
                .toList();

        for (Class<? extends Annotation> uclazz : unregistered) {
            if (clazz.equals(uclazz) || levels.contains(uclazz)) {
                throw new IllegalStateException("Found circular dependency; Levels: " + levels + " Current: " + clazz + " UClass: " + uclazz);
            }

            if (all.contains(uclazz)) {
                levels.add(clazz);
                safelyRecursiveDiscover(uclazz, all, levels);
                levels.pop();
            }
        }

        getAnnotationManager().discoverAnnotation(clazz);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
