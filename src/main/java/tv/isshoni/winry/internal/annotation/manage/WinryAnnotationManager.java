package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.internal.AnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.WinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinryAnnotationManager extends AnnotationManager implements IWinryAnnotationManager {

    private static AraragiLogger LOGGER;

    private final Map<Class<? extends Annotation>, List<IWinryAnnotationProcessor<?>>> annotationProcessors;

    private final IBootstrapper bootstrapper;

    public WinryAnnotationManager(ILoggerFactory loggerFactory, IBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
        this.annotationProcessors = new HashMap<>();

        LOGGER = loggerFactory.createLogger("AnnotationManager");

        register(IWinryAnnotationProcessor.class, (annotation, processor) -> new WinryPreparedAnnotationProcessor(annotation, (IWinryAnnotationProcessor<Annotation>) processor));
    }

    @Override
    public void initialize(Bootstrap bootstrap) {
        LOGGER.debug("Initializing...");

        LOGGER.debug("Performing annotation discovery...");
        IAnnotationDiscoverer discoverer = new SimpleAnnotationDiscoverer(this);
        discoverer.withPackages("tv.isshoni.winry.annotation");
        discoverer.withPackages(bootstrap.loadPackage());

        LOGGER.debug("Loading parameter supplier annotations...");
        discoverer.discoverParameterAnnotations();

        LOGGER.debug("Loading all other annotations...");
        discoverer.discoverAnnotations();

        LOGGER.debug("Attaching requested processors...");
        discoverer.discoverAttachedProcessors();

        LOGGER.debug("Discovered " + getTotalProcessors() + " annotation processors.");
    }

    @Override
    public void register(Class<? extends Annotation> annotation, IAnnotationProcessor<?>... processors) {
        super.register(annotation, processors);

        this.bootstrapper.getContext().register((Object[]) processors);
    }

    @Override
    public boolean isWinry(IAnnotationProcessor<Annotation> processor) {
        return IWinryAnnotationProcessor.class.isAssignableFrom(processor.getClass());
    }

    @Override
    public boolean isWinry(IPreparedAnnotationProcessor processor) {
        return IWinryPreparedAnnotationProcessor.class.isAssignableFrom(processor.getClass());
    }
}
