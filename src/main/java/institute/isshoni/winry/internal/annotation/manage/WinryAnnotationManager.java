package institute.isshoni.winry.internal.annotation.manage;

import institute.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import institute.isshoni.araragi.annotation.manager.AnnotationManager;
import institute.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import institute.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import institute.isshoni.araragi.exception.Exceptions;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Loader;
import institute.isshoni.winry.api.annotation.meta.SingletonHolder;
import institute.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.bootstrap.WinryEventsProvider;
import institute.isshoni.winry.api.context.IExceptionManager;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.internal.annotation.processor.parameter.WinryContextProcessor;
import institute.isshoni.winry.internal.exception.WinryExceptionManager;
import institute.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import institute.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import institute.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import institute.isshoni.winry.internal.model.annotation.prepare.WinryPreparedAdvancedAnnotationProcessor;
import institute.isshoni.winry.internal.model.annotation.prepare.WinryPreparedAnnotationProcessor;
import institute.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import institute.isshoni.winry.internal.model.bootstrap.IExecutableProvider;

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

    @Override
    public <T> T winryConstruct(IWinryContext context, Class<T> clazz, Object... parameters) {
        IAnnotatedClass annotatedClass = context.getMetaManager().findMeta(clazz);

        if (annotatedClass == null) {
            annotatedClass = context.getMetaManager().generateMeta(clazz);
        }

        return winryConstruct(context, annotatedClass, parameters);
    }

    @Override
    public <T> T winryConstruct(IWinryContext context, IAnnotatedClass annotatedClass, Object... parameters) {
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
    public boolean hasAnnotationWithMarker(Object target) {
        return Streams.to(target.getClass().getDeclaredAnnotations())
                .flatMap(anno -> Streams.to(anno.annotationType().getDeclaredAnnotations())
                        .map(Annotation::annotationType))
                .anyMatch(a -> a.equals(SingletonHolder.class));
    }

    @Override
    public void initialize(IWinryContext context) {
        LOGGER.debug("Initializing...");
        LOGGER.debug("Configuring annotation discoverer...");
        this.annotationDiscoverer.withPackages(getAllLoadedPackages(this.bootstrap));

        LOGGER.debug("Performing annotation discovery...");
        this.discoverProcessor(new WinryContextProcessor(context));
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
            result.add("institute.isshoni.winry.api");
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
