package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.manage.AnnotationManager;
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

    public BootstrappedClass getBootstrappedClass(Class<?> clazz) {
        return this.bootstrappedClasses.get(clazz);
    }

    public Collection<BootstrappedClass> getBootstrappedClasses() {
        return this.bootstrappedClasses.values();
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
        getDeclaringClass(method).addMethod(new BootstrappedMethod(method, this.bootstrapper));
    }

    @Override
    public void bootstrap(Field field) {
        if (!this.annotationManager.hasManagedAnnotation(field)) {
            return;
        }

        LOGGER.info("Bootstrapping Field: " + field.getName());
        getDeclaringClass(field).addField(new BootstrappedField(field, getBootstrappedClass(field.getType()), this.bootstrapper));
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
