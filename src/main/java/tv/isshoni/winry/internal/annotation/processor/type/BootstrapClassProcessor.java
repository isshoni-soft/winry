package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BootstrapClassProcessor implements IWinryAnnotationProcessor<Annotation> {

    private static AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public BootstrapClassProcessor(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        LOGGER = context.getLoggerFactory().createLogger(this.getClass());
    }

    @Override
    public void onDiscovery(Class<Annotation> clazz) {
        LOGGER.info("Performing bootstrap discovery for: " + clazz);
        IWinryAnnotationManager annotationManager = this.context.get().getAnnotationManager();
        IAnnotationDiscoverer discoverer = annotationManager.getAnnotationDiscoverer();

        List<Class<?>> found = Streams.to(discoverer.findWithAnnotations(clazz))
                .filter(c -> Objects.nonNull(annotationManager.discoverConstructor(c, false)))
                .toList();

        if (found.isEmpty()) {
            return;
        }

        LOGGER.debug("Loading singletons ...");
        for (Class<?> c : found) {
            loadSingleton(c);
        }
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }

    private void loadSingleton(Class<?> clazz) {
        IWinryAnnotationManager annotationManager = this.context.get().getAnnotationManager();

        if (this.context.get().hasSingleton(clazz)
                || !annotationManager.hasManagedAnnotation(clazz)) {
            return;
        }

        LOGGER.debug("Loading singleton: ${0}", clazz.getName());

        Set<Class<?>> deps = annotationManager.getAllTypesForConstruction(clazz);

        if (deps.isEmpty()) {
            initializeSingleton(clazz);
            return;
        }

        for (Class<?> dep : deps) {
            loadSingleton(dep);
        }

        initializeSingleton(clazz);
    }

    private void initializeSingleton(Class<?> clazz) {
        IWinryAnnotationManager annotationManager = this.context.get().getAnnotationManager();

        Set<Class<? extends Annotation>> transformers = Streams.to(annotationManager.getAllAnnotationsIn(clazz))
                .filter(ac -> ac.isAnnotationPresent(Transformer.class))
                .collect(Collectors.toSet());

        for (Class<? extends Annotation> transformer : transformers) {
            if (!annotationManager.isManagedAnnotation(transformer)) {
                throw new IllegalStateException("Unable to bootstrap class: " + clazz + " found transformer: "
                        + transformer + " that is not managed (no processor? not found during scan?)");
            }
        }

        try {
            this.context.get().addSingleton(clazz);
        } catch (Throwable e) {
            this.context.get().getExceptionManager().toss(e);
        }
    }
}
