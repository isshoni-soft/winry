package tv.isshoni.winry.api.bootstrap;

import org.reflections8.Reflections;
import tv.isshoni.araragi.async.AsyncManager;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.context.WinryContext;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.internal.annotation.manage.InjectionRegistry;
import tv.isshoni.winry.internal.annotation.manage.WinryAnnotationManager;
import tv.isshoni.winry.internal.bootstrap.ElementBootstrapper;
import tv.isshoni.winry.internal.event.WinryEventBus;
import tv.isshoni.winry.internal.logging.LoggerFactory;
import tv.isshoni.winry.reflection.ReflectionUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleBootstrapper implements IBootstrapper {

    private static AraragiLogger LOGGER;

    private final IWinryContext context;

    private Map<Class<?>, Object> provided;

    public SimpleBootstrapper(Bootstrap bootstrap) {
        LoggerFactory loggerFactory = new LoggerFactory();
        loggerFactory.setDefaultLoggerLevel(bootstrap.defaultLevel());
        WinryAnnotationManager annotationManager = new WinryAnnotationManager(loggerFactory, this);
        AsyncManager asyncManager = new AsyncManager();
        ElementBootstrapper elementBootstrapper = new ElementBootstrapper(this, annotationManager, loggerFactory);

        this.context = WinryContext.builder(bootstrap, this)
                .annotationManager(annotationManager)
                .loggerFactory(loggerFactory)
                .asyncManager(asyncManager)
                .elementBootstrapper(elementBootstrapper)
                .eventBus(new WinryEventBus(asyncManager, loggerFactory))
                .injectionRegistry(new InjectionRegistry(elementBootstrapper))
                .build();

        LOGGER = this.context.getLoggerFactory().createLogger("SimpleBootstrapper");
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public Map<Class<?>, Object> getProvided() {
        return Collections.unmodifiableMap(this.provided);
    }

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        this.getContext().getAnnotationManager().initialize(bootstrap);

        provided.values().forEach(this.getContext()::registerToContext);

        this.provided = Collections.unmodifiableMap(provided);

        LOGGER.debug("${dashes%50} Element Bootstrapping ${dashes%50}");
        bootstrapClasses(clazz, this.context.getAnnotationManager().getAllManuallyLoaded(bootstrap), this.context.getAnnotationManager().getAllLoadedPackages(bootstrap), provided);
        LOGGER.debug("Finished class discovery and instantiation...");

        List<IExecutable> run = compileRunList();
        Streams.to(this.context.getAnnotationManager().getAllProviders(bootstrap))
                .map(ReflectionUtil::construct)
                .peek(this.context::registerToContext)
                .flatMap(p -> p.provideExecutables(this.context).stream())
                .peek(this.context::registerToContext)
                .peek(p -> LOGGER.debug("Injecting Executable: " + p.getDisplay()))
                .addTo(run);

        Collections.sort(run);
        LOGGER.debug("${dashes%50} Run Order ${dashes%50}");
        run.forEach(r -> LOGGER.debug(r.getDisplay()));
        LOGGER.info("${dashes%50} Execution ${dashes%50}");
        run.forEach(e -> {
            LOGGER.info("Executing: " + e.getDisplay());
            e.execute();
        });
    }

    @Override
    public List<IExecutable> compileRunList() {
        LOGGER.debug("Compiling run order...");
        return Streams.to(this.getContext().getElementBootstrapper().getBootstrappedClasses())
                .peek((c -> {
                    LOGGER.debug("Finalizing: " + c.getBootstrappedElement().getName());

                    Streams.to(c.getBootstrappedElement().getDeclaredFields())
                            .filter(this.getContext().getAnnotationManager()::hasManagedAnnotation)
                            .forEach(this.getContext().getElementBootstrapper()::bootstrap);
                    LOGGER.debug("Discovered " + c.getFields().size() + " fields");

                    Streams.to(c.getBootstrappedElement().getDeclaredMethods())
                            .filter(this.getContext().getAnnotationManager()::hasManagedAnnotation)
                            .forEach(this.getContext().getElementBootstrapper()::bootstrap);
                    LOGGER.debug("Discovered " + c.getMethods().size() + " methods");
                }))
                .expand(IBootstrappedElement.class, BootstrappedClass::getMethods, BootstrappedClass::getFields)
                .peek(IBootstrappedElement::transform)
                .cast(IExecutable.class)
                .collect(Collectors.toList());
    }

    @Override
    public void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided) {
        LOGGER.debug("Performing class discovery...");

        Set<Class<?>> classes = new HashSet<>();
        classes.add(baseClass);
        classes.addAll(provided.keySet());
        classes.addAll(Arrays.asList(manual));

        LOGGER.debug("Discovered " + manual.length + " manually loaded classes!");
        LOGGER.debug("Discovered " + provided.keySet() + " provided classes!");
        LOGGER.debug("Searching " + Arrays.toString(packages) + " packages for classes...");

        if (packages.length > 0) {
            Reflections reflections = ReflectionUtil.classFinder(packages, manual);

            Streams.to(this.getContext().getAnnotationManager().getManagedAnnotations())
                    .flatMap(a -> reflections.getTypesAnnotatedWith(a).stream())
                    .addTo(classes);
        }

        LOGGER.debug("Discovered " + classes.size() + " classes!");
        LOGGER.debug("Performing bootstrap...");
        classes.forEach(this.getContext().getElementBootstrapper()::bootstrap);

        LOGGER.debug("Attaching provided instances...");
        provided.forEach((c, o) -> {
            BootstrappedClass bootstrapped = this.getContext().getElementBootstrapper().getBootstrappedClass(c);

            bootstrapped.setProvided(true);
            bootstrapped.setObject(o);
        });
    }
}
