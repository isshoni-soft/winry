package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.manager.AnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.bootstrap.WinryEventsProvider;
import tv.isshoni.winry.api.context.IExceptionManager;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.annotation.processor.parameter.WinryContextProcessor;
import tv.isshoni.winry.internal.exception.WinryExceptionManager;
import tv.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.annotation.prepare.WinryPreparedAdvancedAnnotationProcessor;
import tv.isshoni.winry.internal.model.annotation.prepare.WinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.bootstrap.IExecutableProvider;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WinryAnnotationManager extends AnnotationManager implements IWinryAnnotationManager {

    private static AraragiLogger LOGGER;

    private final IBootstrapper bootstrapper;

    private final WinryAnnotationDiscoverer annotationDiscoverer;

    private final IExceptionManager exceptionManager;

    private final Bootstrap bootstrap;

    public WinryAnnotationManager(Bootstrap bootstrap, ILoggerFactory loggerFactory, IBootstrapper bootstrapper) {
        this.bootstrap = bootstrap;
        this.bootstrapper = bootstrapper;
        this.exceptionManager = new WinryExceptionManager(this, loggerFactory);
        this.annotationDiscoverer = new WinryAnnotationDiscoverer(this, loggerFactory);

        LOGGER = loggerFactory.createLogger("AnnotationManager");

        register(IWinryAnnotationProcessor.class, (annotation, element, processor, manager) -> new WinryPreparedAnnotationProcessor(annotation, element, (IWinryAnnotationProcessor<Annotation>) processor, manager));
        register(IWinryAdvancedAnnotationProcessor.class, (annotation, element, processor, manager) -> new WinryPreparedAdvancedAnnotationProcessor(annotation, element, (IWinryAdvancedAnnotationProcessor<Annotation, Object>) processor, manager));
    }

    public void setAnnotationDiscovererContext(IWinryContext context) {
        this.annotationDiscoverer.setContext(context);
    }

    @Override
    public <T> T winryConstruct(IWinryContext context, Class<T> clazz, Object... parameters) {
        IAnnotatedClass annotatedClass = context.getMetaManager().findMeta(clazz);

        if (annotatedClass == null) {
            annotatedClass = context.getMetaManager().generateMeta(clazz);
        }

        if (!annotatedClass.isTransformed()) {
            annotatedClass.transform(new WinryWrapperGenerator(context, annotatedClass));
        }

        T result;
        try {
            result = (T) annotatedClass.newInstance(parameters);
        } catch (Throwable e) {
            throw Exceptions.rethrow(e);
        }

        annotatedClass.regenerate(result);
        annotatedClass.execute(result);
        annotatedClass.getMethods().forEach(meta -> meta.execute(result));
        annotatedClass.getFields().forEach(meta -> meta.execute(result));

        return result;
    }

    @Override
    public void initialize() {
        LOGGER.debug("Initializing...");
        LOGGER.debug("Configuring annotation discoverer...");
        this.annotationDiscoverer.withPackages(getAllLoadedPackages(this.bootstrap));

        LOGGER.debug("Performing annotation discovery...");
        this.discoverProcessor(new WinryContextProcessor(this.annotationDiscoverer.getContext().get())); // TODO: remove techdebt, just move context up to annotation manager.
        this.annotationDiscoverer.discoverAnnotations();

        LOGGER.debug("Attaching requested processors...");
        this.annotationDiscoverer.discoverAttachedProcessors();

        LOGGER.debug("Discovered " + getManagedAnnotations().size() + " annotations and " + getTotalProcessors() + " annotation processors.");
        LOGGER.debug("Done initializing!");
    }

    @Override
    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public String[] getAllLoadedPackages(Bootstrap bootstrap) {
        ArrayList<String> result = new ArrayList<>(Arrays.asList(bootstrap.loader().loadPackage()));

        if (!bootstrap.disableDefaultPackage()) {
            result.add("tv.isshoni.winry.api");
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
    public String[] getAllLoadedPackages() {
        return this.getAllLoadedPackages(this.bootstrap);
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
    public Class<?>[] getAllManuallyLoaded() {
        return this.getAllManuallyLoaded(this.bootstrap);
    }

    @Override
    public IAnnotationDiscoverer getAnnotationDiscoverer() {
        return this.annotationDiscoverer;
    }

    @Override
    public IExceptionManager getExceptionManager() {
        return this.exceptionManager;
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
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
    public Class<? extends IExecutableProvider>[] getAllProviders() {
        return this.getAllProviders(this.bootstrap);
    }

    @Override
    protected void postRegisterProcessors(Class<? extends Annotation> annotation, Collection<IAnnotationProcessor<Annotation>> processors) {
        this.bootstrapper.getContext().registerToContext(processors.toArray(Object[]::new));
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
