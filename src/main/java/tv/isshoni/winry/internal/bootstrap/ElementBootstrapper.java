package tv.isshoni.winry.internal.bootstrap;

import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.internal.bytebuddy.ClassTransformingBlueprint;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;
import tv.isshoni.winry.internal.entity.logging.ILoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
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

    private final IExceptionManager exceptionManager;

    public ElementBootstrapper(IBootstrapper bootstrapper, IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory, IExceptionManager exceptionManager) {
        this.bootstrapper = bootstrapper;
        this.annotationManager = annotationManager;
        this.exceptionManager = exceptionManager;
        this.bootstrappedClasses = new TypeMap<>();
        this.bootstrappedMethods = new HashMap<>();
        this.bootstrappedFields = new HashMap<>();

        LOGGER = loggerFactory.createLogger("ElementBootstrapper");
    }

    @Override
    public ITransformingBlueprint supplyTransformingBlueprint(BootstrappedClass bootstrappedClass) {
        return new ClassTransformingBlueprint(bootstrappedClass, this.exceptionManager);
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
    public BootstrappedClass bootstrap(Class<?> clazz) {
        BootstrappedClass result;
        if (this.bootstrappedClasses.containsKey(clazz)) {
            LOGGER.debug("Reloading Class: " + clazz.getName());
            result = this.bootstrappedClasses.get(clazz);
            result.build();
        } else {
            LOGGER.debug("Bootstrapping Class: " + clazz.getName());
            result = new BootstrappedClass(clazz, this.bootstrapper, this.bootstrapper.getContext());
            this.bootstrappedClasses.put(clazz, result);
            this.bootstrapper.getContext().registerToContext(result);
        }

        return result;
    }

    @Override
    public BootstrappedMethod bootstrap(Method method) {
        BootstrappedMethod result;
        if (this.bootstrappedMethods.containsKey(method)) {
            LOGGER.debug("Reloading Method: " + method.getName());
            result = this.bootstrappedMethods.get(method);
            result.compileAnnotations();
        } else {
            LOGGER.debug("Bootstrapping Method: " + method.getName());
            result = new BootstrappedMethod(method, this.bootstrapper);
            this.bootstrappedMethods.put(method, result);
            this.bootstrapper.getContext().registerToContext(result);
        }

        return result;
    }

    @Override
    public BootstrappedField bootstrap(Field field) {
        BootstrappedField result;

        if (this.bootstrappedFields.containsKey(field)) {
            LOGGER.debug("Reloading Field: " + field.getName());
            result = this.bootstrappedFields.get(field);
            result.compileAnnotations();
        } else {
            LOGGER.debug("Bootstrapping Field: " + field.getName());
            result = new BootstrappedField(field, getBootstrappedClass(field.getType()), this.bootstrapper);
            this.bootstrappedFields.put(field, result);
            this.bootstrapper.getContext().registerToContext(result);
        }

        return result;
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
        Class<T> constructed = (Class<T>) bootstrapped.findClass();

        LOGGER.debug("Constructing: " + constructed);

        try {
            return this.annotationManager.construct(constructed);
        } catch (Throwable e) {
            throw Exceptions.rethrow(e);
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


        LOGGER.debug("Executing: " + bootstrapped.getDeclaringClass().findClass() + " -- " + method);
        try {
            T result = this.annotationManager.execute(method, target, runtimeContext);
            bootstrapped.setExecuted(true);

            return result;
        } catch (Throwable e) {
            throw Exceptions.rethrow(e);
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
