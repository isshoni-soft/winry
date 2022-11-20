package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.AraragiUpstream;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BootstrapClassProcessor implements IWinryAnnotationProcessor<Annotation> {

    private static AraragiLogger LOGGER;

    private final IWinryContext context;

    public BootstrapClassProcessor(@Context IWinryContext context) {
        this.context = context;
        LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void onDiscovery(Class<Annotation> clazz) {
        LOGGER.info("Performing bootstrap discovery for: " + clazz);
        IWinryAnnotationManager annotationManager = this.context.getAnnotationManager();
        IAnnotationDiscoverer discoverer = annotationManager.getAnnotationDiscoverer();

        List<Class<?>> found = Streams.to(discoverer.findWithAnnotations(clazz))
                .filter(c -> Objects.nonNull(annotationManager.discoverConstructor(c)))
                .sorted((first, second) -> {
                    Set<Class<? extends Annotation>> firstAnno = annotationManager.getAllAnnotationsForConstruction(first);
                    Set<Class<? extends Annotation>> secondAnno = annotationManager.getAllAnnotationsForConstruction(second);

                    int simpleCompare = AraragiUpstream.simpleCompare(firstAnno, secondAnno, Set::isEmpty);

                    if (simpleCompare != 2) {
                        return simpleCompare;
                    }

                    Set<Class<?>> firstDeps = annotationManager.getAllTypesForConstruction(first);
                    Set<Class<?>> secondDeps = annotationManager.getAllTypesForConstruction(second);

                    if (firstDeps.isEmpty() && secondDeps.isEmpty()) {
                        return 0;
                    }

                    if (secondDeps.contains(first) && firstDeps.contains(second)) {
                        throw new IllegalStateException("Circular dependency found; " + first + " (" + firstDeps + ") - " + second + "(" + secondDeps + ")");
                    }

                    if (secondDeps.contains(first)) {
                        return 1;
                    } else if (firstDeps.contains(second)) {
                        return -1;
                    }

                    return 1;
                })
                .toList();

        if (found.isEmpty()) {
            return;
        }

        LOGGER.debug("Bootstrap order:");
        found.forEach(c -> LOGGER.debug("-> " + c.getName()));

        found.forEach(c -> {
            Set<Class<? extends Annotation>> transformers = Streams.to(annotationManager.getAllAnnotationsIn(c))
                    .filter(ac -> ac.isAnnotationPresent(Transformer.class))
                    .collect(Collectors.toSet());

            BootstrappedClass bootstrappedClass = annotationManager.getElementBootstrapper().bootstrap(c);

            for (Class<? extends Annotation> transformer : transformers) {
                if (!annotationManager.isManagedAnnotation(transformer)) {
                    throw new IllegalStateException("Unable to bootstrap class: " + c + " found transformer: " + transformer + " that is not managed (no processor? not found during scan?)");
                }
            }
            bootstrappedClass.transform();

            Object instance = bootstrappedClass.newInstance();

            bootstrappedClass.getBootstrapper().getContext().registerToContext(instance);
            bootstrappedClass.setObject(instance);
        });
    }
}
