package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BootstrappedClass implements IBootstrappedElement<Class<?>> {

    private final IBootstrapper bootstrapper;

    private final AnnotationManager annotationManager;

    private final Class<?> clazz;
    private Class<?> wrappedClazz;

    private Object object;

    private final Collection<Annotation> annotations;

    private final List<BootstrappedField> fields;

    private final List<BootstrappedMethod> methods;

    private final Set<ReflectedModifier> modifiers;

    private boolean provided = false;
    private boolean injectable = true;

    public BootstrappedClass(Class<?> clazz, IBootstrapper bootstrapper) {
        this.clazz = clazz;
        this.bootstrapper = bootstrapper;
        this.modifiers = ReflectedModifier.getModifiers(clazz);
        this.annotationManager = bootstrapper.getAnnotationManager();
        this.annotations = this.annotationManager.getManagedAnnotationsOn(clazz);
        this.fields = new LinkedList<>();
        this.methods = new LinkedList<>();

        if (this.annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.clazz.getSimpleName() + " has conflicting annotations! " + this.annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    public void addField(BootstrappedField field) {
        this.fields.add(field);
    }

    public void addField(Collection<BootstrappedField> fields) {
        this.fields.addAll(fields);
    }

    public void addMethod(BootstrappedMethod method) {
        this.methods.add(method);
    }

    public void addMethod(Collection<BootstrappedMethod> methods) {
        this.methods.addAll(methods);
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
    }

    public void setWrappedClass(Class<?> wrappedClazz) {
        this.wrappedClazz = wrappedClazz;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setInjectable(boolean injectable) {
        this.injectable = injectable;
    }

    @Override
    public Class<?> getBootstrappedElement() {
        return this.clazz;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifiers);
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    @Override
    public String getDisplay() {
        return this.clazz.getName();
    }

    @Override
    public int getWeight() {
        if (this.isProvided()) {
            return Integer.MAX_VALUE - 50500;
        }

        return IBootstrappedElement.super.getWeight();
    }

    @Override
    public void execute() {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.executeClass(this);
        }
    }

    public Object getObject() {
        return this.object;
    }

    public boolean isProvided() {
        return this.provided;
    }

    public boolean isInjectable() {
        return this.injectable;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    public List<BootstrappedField> getFields() {
        return ImmutableList.copyOf(this.fields);
    }

    public List<BootstrappedMethod> getMethods() {
        return ImmutableList.copyOf(this.methods);
    }

    public Class<?> getWrappedClass() {
        return this.wrappedClazz;
    }

    public boolean hasWrappedClass() {
        return Objects.nonNull(this.wrappedClazz);
    }

    public boolean hasObject() {
        return Objects.nonNull(this.object);
    }

    @Override
    public String toString() {
        return "BootstrappedClass[class=" + this.clazz.getName() + ",bootstrapped=" + this.hasObject() + ",weight=" + this.getWeight() + "]";
    }
}
