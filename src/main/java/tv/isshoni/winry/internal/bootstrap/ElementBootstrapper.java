package tv.isshoni.winry.internal.bootstrap;

import tv.isshoni.araragi.data.collection.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.entity.logging.ILoggerFactory;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionUtil;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ElementBootstrapper implements IElementBootstrapper {

    private static AraragiLogger LOGGER;

    private final Map<Class<?>, BootstrappedClass> bootstrappedClasses;

    private final Map<Method, BootstrappedMethod> bootstrappedMethods;

    private final Map<Field, BootstrappedField> bootstrappedFields;

    private final IBootstrapper bootstrapper;

    private final IWinryAnnotationManager annotationManager;

    public ElementBootstrapper(IBootstrapper bootstrapper, IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        this.bootstrapper = bootstrapper;
        this.annotationManager = annotationManager;
        this.bootstrappedClasses = new TypeMap<>();
        this.bootstrappedMethods = new HashMap<>();
        this.bootstrappedFields = new HashMap<>();

        LOGGER = loggerFactory.createLogger("ElementBootstrapper");
    }

    @Override
    public ITransformingBlueprint supplyTransformingBlueprint(BootstrappedClass bootstrappedClass) {
        return new ClassTransformingBlueprint(bootstrappedClass);
    }

    @Override
    public BootstrappedClass getBootstrappedClass(Class<?> clazz) {
        return this.bootstrappedClasses.get(clazz);
    }

    @Override
    public BootstrappedMethod getBootstrappedMethod(Method method) {
        return this.bootstrappedMethods.get(method);
    }

    @Override
    public BootstrappedField getBootstrappedField(Field field) {
        return this.bootstrappedFields.get(field);
    }

    @Override
    public Collection<BootstrappedClass> getBootstrappedClasses() {
        return this.bootstrappedClasses.values();
    }

    @Override
    public Collection<BootstrappedMethod> getBootstrappedMethod() {
        return this.bootstrappedMethods.values();
    }

    @Override
    public Collection<BootstrappedField> getBootstrappedField() {
        return this.bootstrappedFields.values();
    }

    @Override
    public void bootstrap(Class<?> clazz) {
        if (!this.annotationManager.hasManagedAnnotation(clazz)) {
            return;
        }

        LOGGER.debug("Bootstrapping Class: " + clazz.getName());
        this.bootstrappedClasses.put(clazz, new BootstrappedClass(clazz, this.bootstrapper));
    }

    @Override
    public void bootstrap(Method method) {
        if (!this.annotationManager.hasManagedAnnotation(method)) {
            return;
        }

        LOGGER.debug("Bootstrapping Method: " + method.getName());
        BootstrappedMethod bootstrappedMethod = new BootstrappedMethod(method, this.bootstrapper);

        getDeclaringClass(method).addMethod(bootstrappedMethod);

        this.bootstrappedMethods.put(method, bootstrappedMethod);
    }

    @Override
    public void bootstrap(Field field) {
        if (!this.annotationManager.hasManagedAnnotation(field)) {
            return;
        }

        LOGGER.debug("Bootstrapping Field: " + field.getName());
        BootstrappedField bootstrappedField = new BootstrappedField(field, getBootstrappedClass(field.getType()), this.bootstrapper);

        getDeclaringClass(field).addField(bootstrappedField);

        this.bootstrappedFields.put(field, bootstrappedField);
    }

    @Override
    public BootstrappedClass getDeclaringClass(Member member) {
        BootstrappedClass bootstrappedClass = getBootstrappedClass(member.getDeclaringClass());

        if (bootstrappedClass == null) {
            throw new IllegalStateException("Unable to find " + member.getDeclaringClass() + " in class registry!");
        }

        return bootstrappedClass;
    }

    @Override
    public <T> T construct(BootstrappedClass bootstrapped) {
        Class<T> clazz = (Class<T>) bootstrapped.getBootstrappedElement();
        Class<T> constructed = (bootstrapped.hasWrappedClass() ? (Class<T>) bootstrapped.getWrappedClass() : clazz);

        Constructor<T> constructor = (Constructor<T>) this.annotationManager.discoverConstructor(constructed);

        if (Objects.isNull(constructor)) {
            throw new RuntimeException("Constructor for " + constructed + " is null!");
        }

        LOGGER.debug("Class: new " + constructed.getName() + "()");
        try {
            return this.annotationManager.execute(constructor, null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T execute(BootstrappedMethod bootstrapped) {
        return execute(bootstrapped, new HashMap<>());
    }

    @Override
    public <T> T execute(BootstrappedMethod bootstrapped, Map<String, Object> runtimeContext) {
        Method method = bootstrapped.getBootstrappedElement();
        Object target = null;

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = getDeclaringClass(method).getObject();

            if (Objects.isNull(target)) {
                LOGGER.error("Non-static target is null for: " + bootstrapped.getDisplay());
            }
        }

        try {
            T result = this.annotationManager.execute(method, target, runtimeContext);
            bootstrapped.setExecuted(true);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void inject(BootstrappedField bootstrapped, Object injected) {
        Object target = null;
        Field field = bootstrapped.getBootstrappedElement();

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = getDeclaringClass(field).getObject();

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
