package tv.isshoni.winry.internal.annotation.manage;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.internal.AnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.annotation.WinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        ArrayList<String> packages = new ArrayList<>(Arrays.asList(bootstrap.loadPackage()));
        packages.add("tv.isshoni.winry.annotation");

        LOGGER.debug("Performing annotation discovery...");

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
                .forPackages(packages.toArray(new String[0])));

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Processor.class);

        classes.stream()
                .map(c -> (Class<? extends Annotation>) c)
                .filter(c -> c.isAnnotationPresent(Processor.class))
                .forEach(this::discover);

        classes = reflections.getTypesAnnotatedWith(AttachTo.class);

        classes.stream()
                .filter(c -> c.isAnnotationPresent(AttachTo.class))
                .filter(IAnnotationProcessor.class::isAssignableFrom)
                .map(c -> (Class<IWinryAnnotationProcessor<?>>) c)
                .forEach(c -> register(c.getAnnotation(AttachTo.class).value(), c));

        LOGGER.debug("Discovered " + getTotalProcessors() + " annotation processors.");
    }

    @Override
    public boolean isWinry(IAnnotationProcessor<Annotation> processor) {
        return IWinryAnnotationProcessor.class.isAssignableFrom(processor.getClass());
    }

    @Override
    public boolean isWinry(IPreparedAnnotationProcessor processor) {
        return IWinryPreparedAnnotationProcessor.class.isAssignableFrom(processor.getClass());
    }

    @Override
    public IAnnotationProcessor<?> construct(Class<? extends IAnnotationProcessor<?>> processor) {
        IAnnotationProcessor<?> result;

        try {
            // TODO: REPLACE ME WITH REFERENCES TO THE SUPPLIER SYSTEM
            result = processor.getConstructor(IWinryContext.class).newInstance(this.bootstrapper.getContext());
        } catch (NoSuchMethodException e) {
            result = super.construct(processor);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e); // TODO: Add a specialized exception
        }

        this.bootstrapper.getContext().register(result);

        return result;
    }
}
