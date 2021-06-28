package tv.isshoni.winry.internal.bootstrap;

import tv.isshoni.winry.internal.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ElementBootstrapper implements IElementBootstrapper {

    private static final WinryLogger LOGGER = WinryLogger.create("ElementBootstrapper");

    private final Map<Class<?>, BootstrappedClass> bootstrappedClasses;

    private final Map<Method, BootstrappedMethod> bootstrappedMethods;

    private final Map<Field, BootstrappedField> bootstrappedFields;

    private final IBootstrapper bootstrapper;

    private final AnnotationManager annotationManager;

    public ElementBootstrapper(IBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
        this.annotationManager = bootstrapper.getAnnotationManager();
        this.bootstrappedClasses = new HashMap<>();
        this.bootstrappedMethods = new HashMap<>();
        this.bootstrappedFields = new HashMap<>();
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

        LOGGER.info("Bootstrapping Class: " + clazz.getName());
        this.bootstrappedClasses.put(clazz, new BootstrappedClass(clazz, this.bootstrapper));
    }

    @Override
    public void bootstrap(Method method) {
        if (!this.annotationManager.hasManagedAnnotation(method)) {
            return;
        }

        LOGGER.info("Bootstrapping Method: " + method.getName());
        BootstrappedMethod bootstrappedMethod = new BootstrappedMethod(method, this.bootstrapper);

        getDeclaringClass(method).addMethod(bootstrappedMethod);

        this.bootstrappedMethods.put(method, bootstrappedMethod);
    }

    @Override
    public void bootstrap(Field field) {
        if (!this.annotationManager.hasManagedAnnotation(field)) {
            return;
        }

        LOGGER.info("Bootstrapping Field: " + field.getName());
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
}
