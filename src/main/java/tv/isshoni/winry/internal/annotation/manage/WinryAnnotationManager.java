package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.internal.AnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.bootstrap.WinryEventsProvider;
import tv.isshoni.winry.entity.annotation.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.prepare.WinryPreparedAdvancedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.prepare.WinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IExecutableProvider;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;

public class WinryAnnotationManager extends AnnotationManager implements IWinryAnnotationManager {

    private static AraragiLogger LOGGER;

    private final IBootstrapper bootstrapper;

    public WinryAnnotationManager(ILoggerFactory loggerFactory, IBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;

        LOGGER = loggerFactory.createLogger("AnnotationManager");

        register(IWinryAnnotationProcessor.class, (annotation, element, processor) -> new WinryPreparedAnnotationProcessor(annotation, element, (IWinryAnnotationProcessor<Annotation>) processor));
        register(IWinryAdvancedAnnotationProcessor.class, (annotation, element, processor) -> new WinryPreparedAdvancedAnnotationProcessor(annotation, element, (IWinryAdvancedAnnotationProcessor<Annotation, Object>) processor));
    }

    @Override
    public void initialize(Bootstrap bootstrap) {
        LOGGER.debug("Initializing...");
        LOGGER.debug("Performing annotation discovery...");

        IAnnotationDiscoverer discoverer = new SimpleAnnotationDiscoverer(this);
        discoverer.withPackages(getAllLoadedPackages(bootstrap));

        LOGGER.debug("Loading parameter supplier annotations...");
        discoverer.discoverParameterAnnotations();

        LOGGER.debug("Loading all other annotations...");
        discoverer.discoverAnnotations();

        LOGGER.debug("Attaching requested processors...");
        discoverer.discoverAttachedProcessors();

        LOGGER.debug("Discovered " + getManagedAnnotations().size() + " annotations and " + getTotalProcessors() + " annotation processors.");
        LOGGER.debug("Done initializing!");
    }

    @Override
    public String[] getAllLoadedPackages(Bootstrap bootstrap) {
        ArrayList<String> result = new ArrayList<>(Arrays.asList(bootstrap.loader().loadPackage()));

        if (!bootstrap.disableDefaultPackage()) {
            result.add("tv.isshoni.winry.api.annotation");
            result.add("tv.isshoni.winry.api.service");
        }

        Streams.to(bootstrap.loader().manualLoad())
                .filter(c -> c.isAnnotationPresent(Loader.class))
                .map(c -> c.getAnnotation(Loader.class))
                .flatMap(l -> Streams.to(l.loadPackage()))
                .distinct()
                .forEach(result::add);

        return result.toArray(new String[0]);
    }

    @Override
    public Class<?>[] getAllManuallyLoaded(Bootstrap bootstrap) {
        ArrayList<Class<?>> result = new ArrayList<>(Arrays.asList(bootstrap.loader().manualLoad()));

        Streams.to(bootstrap.loader().manualLoad())
                .filter(c -> c.isAnnotationPresent(Loader.class))
                .map(c -> c.getAnnotation(Loader.class))
                .flatMap(l -> Streams.to(l.manualLoad()))
                .distinct()
                .forEach(result::add);

        return result.toArray(new Class<?>[0]);
    }

    @Override
    public Class<? extends IExecutableProvider>[] getAllProviders(Bootstrap bootstrap) {
        ArrayList<Class<? extends IExecutableProvider>> result = new ArrayList<>(Arrays.asList(bootstrap.loader().providers()));

        if (!bootstrap.disableDefaultProvider()) {
            result.add(WinryEventsProvider.class);
        }

        Streams.to(bootstrap.loader().manualLoad())
                .filter(c -> c.isAnnotationPresent(Loader.class))
                .map(c -> c.getAnnotation(Loader.class))
                .flatMap(l -> Streams.to(l.providers()))
                .distinct()
                .forEach(result::add);

        return result.toArray(new Class[0]);
    }

    @Override
    public void register(Class<? extends Annotation> annotation, IAnnotationProcessor<?>... processors) {
        super.register(annotation, processors);

        this.bootstrapper.getContext().registerToContext((Object[]) processors);
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
