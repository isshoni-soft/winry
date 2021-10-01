package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.araragi.async.AsyncManager;
import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.context.WinryContext;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.entity.logging.ILoggerFactory;
import tv.isshoni.winry.internal.annotation.manage.WinryAnnotationManager;
import tv.isshoni.winry.internal.bootstrap.ElementBootstrapper;
import tv.isshoni.winry.internal.logging.LoggerFactory;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.reflections8.Reflections;

public class SimpleBootstrapper implements IBootstrapper {

    private static AraragiLogger LOGGER;

    private final IWinryContext context;

    private Map<Class<?>, Object> provided;

    public SimpleBootstrapper(Bootstrap bootstrap) {
        LoggerFactory loggerFactory = new LoggerFactory();
        loggerFactory.setDefaultLoggerLevel(bootstrap.defaultLevel());
        WinryAnnotationManager annotationManager = new WinryAnnotationManager(loggerFactory, this);

        this.context = new WinryContext(this, new AsyncManager(), annotationManager, loggerFactory, new ElementBootstrapper(this, annotationManager, loggerFactory), bootstrap);

        LOGGER = this.context.getLoggerFactory().createLogger("SimpleBootstrapper");
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return this.context.getAnnotationManager();
    }

    @Override
    public IElementBootstrapper getElementBootstrapper() {
        return this.context.getElementBootstrapper();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.context.getLoggerFactory();
    }

    @Override
    public IAsyncManager getAsyncManager() {
        return this.context.getAsyncManager();
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
        this.getAnnotationManager().initialize(bootstrap);

        provided.values().forEach(this.getContext()::registerToContext);

        this.provided = Collections.unmodifiableMap(provided);

        LOGGER.debug("${dashes%50} Element Bootstrapping ${dashes%50}");
        bootstrapClasses(clazz, bootstrap.manualLoad(), bootstrap.loadPackage(), provided);
        LOGGER.debug("Finished class discovery and instantiation...");

        List<IExecutable> run = compileRunList();
        LOGGER.debug("---------------------------------------- Run Order ----------------------------------------");
        run.forEach(r -> LOGGER.debug(r.getDisplay()));
        LOGGER.debug("---------------------------------------- Execution ----------------------------------------");
        run.forEach(e -> {
            LOGGER.debug("Executing: " + e.getDisplay());
            e.execute();
        });
    }

    @Override
    public List<IExecutable> compileRunList() {
        LOGGER.debug("Compiling run order...");
        return Streams.to(this.getElementBootstrapper().getBootstrappedClasses())
                .peek((c -> {
                    LOGGER.debug("Finalizing: " + c.getBootstrappedElement().getName());

                    Arrays.stream(c.getBootstrappedElement().getDeclaredFields())
                            .filter(this.getAnnotationManager()::hasManagedAnnotation)
                            .forEach(this.getElementBootstrapper()::bootstrap);
                    LOGGER.debug("Discovered " + c.getFields().size() + " fields");

                    Arrays.stream(c.getBootstrappedElement().getDeclaredMethods())
                            .filter(this.getAnnotationManager()::hasManagedAnnotation)
                            .forEach(this.getElementBootstrapper()::bootstrap);
                    LOGGER.debug("Discovered " + c.getMethods().size() + " methods");
                }))
                .expand(IBootstrappedElement.class, BootstrappedClass::getMethods, BootstrappedClass::getFields)
                .peek(IBootstrappedElement::transform)
                .sorted()
                .cast(IExecutable.class)
                .toList();
    }

    @Override
    public <T> T construct(BootstrappedClass bootstrapped) {
        Class<T> clazz = (Class<T>) bootstrapped.getBootstrappedElement();
        Class<T> constructed = (bootstrapped.hasWrappedClass() ? (Class<T>) bootstrapped.getWrappedClass() : clazz);

        Constructor<T> constructor = (Constructor<T>) getAnnotationManager().discoverConstructor(constructed);

        if (Objects.isNull(constructor)) {
            throw new RuntimeException("Constructor for " + constructed + " is null!");
        }

        LOGGER.debug("Class: new " + constructed.getName() + "()");
        return ReflectionUtil.construct(constructor, getAnnotationManager().prepareExecutable(constructor));
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

            this.getAnnotationManager().getManagedAnnotations().stream()
                    .flatMap(a -> reflections.getTypesAnnotatedWith(a).stream())
                    .forEach(classes::add);
        }

        LOGGER.debug("Discovered " + classes.size() + " classes!");
        LOGGER.debug("Performing bootstrap...");
        classes.forEach(this.getElementBootstrapper()::bootstrap);

        LOGGER.debug("Attaching provided instances...");
        provided.forEach((c, o) -> {
            BootstrappedClass bootstrapped = this.getElementBootstrapper().getBootstrappedClass(c);

            bootstrapped.setProvided(true);
            bootstrapped.setObject(o);
        });
    }

    @Override
    public <T> T execute(BootstrappedMethod bootstrapped) {
        Method method = bootstrapped.getBootstrappedElement();
        Object target = null;

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = this.getElementBootstrapper().getDeclaringClass(method).getObject();
        }

        try {
            T result;

            if (method.getParameterCount() > 0) {
                result = (T) method.invoke(target, getAnnotationManager().prepareExecutable(method));
            } else {
                result = (T) method.invoke(target);
            }

            bootstrapped.setExecuted(true);

            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void inject(BootstrappedField bootstrapped, Object injected) {
        Object target = null;
        Field field = bootstrapped.getBootstrappedElement();

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = this.getElementBootstrapper().getDeclaringClass(field).getObject();

            if (target == null) {
                throw new RuntimeException("Tried injecting into null instance " + bootstrapped.getDisplay());
            }
        }

        ReflectionUtil.injectField(field, target, injected);
        bootstrapped.setInjected(true);
    }

    @Override
    public void inject(BootstrappedField bootstrapped) {
        inject(bootstrapped, bootstrapped.getTarget().getObject());
    }
}
