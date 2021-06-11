package tv.isshoni.winry.bootstrap;

import org.reflections8.Reflections;
import tv.isshoni.araragi.stream.AraragiStream;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleBootstrapper implements IBootstrapper {

    private static final WinryLogger LOGGER = WinryLogger.create("SimpleBootstrapper");

    private final AnnotationManager annotationManager;

    private final ElementBootstrapper elementBootstrapper;

    private Map<Class<?>, Object> provided;

    public SimpleBootstrapper() {
        this.annotationManager = new AnnotationManager();
        this.elementBootstrapper = new ElementBootstrapper(this);
    }

    @Override
    public AnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

    @Override
    public ElementBootstrapper getElementBootstrapper() {
        return this.elementBootstrapper;
    }

    @Override
    public Map<Class<?>, Object> getProvided() {
        return Collections.unmodifiableMap(this.provided);
    }

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        this.provided = Collections.unmodifiableMap(provided);

        LOGGER.info("Bootstrapping elements...");
        bootstrapClasses(clazz, bootstrap.manualLoad(), bootstrap.loadPackage(), provided);
        this.compileRunStream().forEachOrdered(e -> {
            LOGGER.info("Executing: " + e);
            e.execute();
        });
//        LOGGER.info("Finished class discovery and instantiation...");
//        LOGGER.info("Boot order:");
    }

    @Override
    public AraragiStream<IBootstrappedElement> compileRunStream() {
        LOGGER.info("Finalizing classes...");
//        this.elementBootstrapper.getBootstrappedClasses().forEach;

        return Streams.to(this.elementBootstrapper.getBootstrappedClasses())
                .peek((c -> {
                    LOGGER.info("Finalizing: " + c.getBootstrappedElement().getName());

                    Arrays.stream(c.getBootstrappedElement().getDeclaredFields())
                            .filter(this.annotationManager::hasManagedAnnotation)
                            .forEach(this.elementBootstrapper::bootstrap);
                    LOGGER.info("Discovered " + c.getFields().size() + " fields");

                    Arrays.stream(c.getBootstrappedElement().getDeclaredMethods())
                            .filter(this.annotationManager::hasManagedAnnotation)
                            .forEach(this.elementBootstrapper::bootstrap);
                    LOGGER.info("Discovered " + c.getMethods().size() + " methods");
                    // TODO: All wrapping will be handled by the bytebuddy interface!
//            LOGGER.info("Wrapping class...");
//            c.setWrappedClass(ByteBuddyUtil.wrapClass(c)
//                    .name("WinryWrapped" + c.getBootstrappedElement().getSimpleName())
//                    .make()
//                    .load(ClassLoader.getSystemClassLoader())
//                    .getLoaded());
                }))
                .expand(IBootstrappedElement.class, BootstrappedClass::getMethods, BootstrappedClass::getFields)
                .sorted();
    }

    @Override
    public void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided) {
        LOGGER.info("Performing class discovery...");

        Set<Class<?>> classes = new HashSet<>();
        classes.add(baseClass);
        classes.addAll(provided.keySet());
        classes.addAll(Arrays.asList(manual));

        LOGGER.info("Discovered " + manual.length + " manually loaded classes!");
        LOGGER.info("Discovered " + provided.keySet() + " provided classes!");
        LOGGER.info("Searching " + Arrays.toString(packages) + " packages for classes...");

        if (packages.length > 0) {
            Reflections reflections = ReflectionUtil.classFinder(packages, manual);

            this.annotationManager.getManagedAnnotations().stream()
                    .flatMap(a -> reflections.getTypesAnnotatedWith(a).stream())
                    .forEach(classes::add);
        }

        LOGGER.info("Discovered " + classes.size() + " classes!");
        LOGGER.info("Performing bootstrap...");
        classes.forEach(this.elementBootstrapper::bootstrap);

        LOGGER.info("Attaching provided instances...");
        provided.forEach((c, o) -> {
            BootstrappedClass bootstrapped = this.elementBootstrapper.getBootstrappedClass(c);

            bootstrapped.setProvided(true);
            bootstrapped.setObject(o);
        });
    }

    public <T> T execute(BootstrappedMethod bootstrapped) {
        Method method = bootstrapped.getBootstrappedElement();
        Object target = null;

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = this.elementBootstrapper.getDeclaringClass(method).getObject();
        }

        try {
            T result = (T) method.invoke(target);

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
            target = this.elementBootstrapper.getDeclaringClass(field).getObject();
        }

        ReflectionUtil.injectField(field, target, injected);
        bootstrapped.setInjected(true);
    }

    @Override
    public void inject(BootstrappedField bootstrapped) {
//        Preconditions.checkNotNull(bootstrapped);
//        Preconditions.checkNotNull(bootstrapped.getTarget());
//        Preconditions.checkNotNull(bootstrapped.getTarget().getObject());

        inject(bootstrapped, bootstrapped.getTarget().getObject());
    }
}
