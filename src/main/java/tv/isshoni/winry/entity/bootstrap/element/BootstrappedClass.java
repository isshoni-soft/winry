package tv.isshoni.winry.entity.bootstrap.element;

import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.entity.context.IContextual;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class BootstrappedClass implements IBootstrappedElement<Class<?>>, IContextual {

    private final AraragiLogger LOGGER;

    private final IBootstrapper bootstrapper;

    private final Class<?> clazz;
    private Class<?> wrappedClazz;

    private Object object;

    private final List<Annotation> annotations;

    private final List<BootstrappedField> fields;

    private final List<BootstrappedMethod> methods;

    private final Set<ReflectedModifier> modifiers;

    private final ITransformingBlueprint transformingBlueprint;

    private boolean provided = false;
    private boolean injectable = true;

    public BootstrappedClass(Class<?> clazz, IBootstrapper bootstrapper) {
        IWinryAnnotationManager annotationManager = bootstrapper.getContext().getAnnotationManager();

        this.LOGGER = bootstrapper.getContext().getLoggerFactory().createLogger("BootstrappedClass");
        this.clazz = clazz;
        this.bootstrapper = bootstrapper;
        this.modifiers = ReflectedModifier.getModifiers(clazz);
        this.annotations = annotationManager.getManagedAnnotationsOn(clazz);
        this.transformingBlueprint = bootstrapper.getContext().getElementBootstrapper().supplyTransformingBlueprint(this);
        this.fields = new LinkedList<>();
        this.methods = new LinkedList<>();

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.clazz.getSimpleName() + " has conflicting annotations! " + annotationManager.getConflictingAnnotations(this.annotations));
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
        LOGGER.debug("Setting wrapped class to: " + wrappedClazz.getName());

        this.wrappedClazz = wrappedClazz;
    }

    public void setObject(Object object) {
        LOGGER.debug("Set Object: " + object);
        this.object = object;
    }

    public void setInjectable(boolean injectable) {
        this.injectable = injectable;
    }

    public Object newInstance() {
        return getBootstrapper().getContext().getElementBootstrapper().construct(this);
    }

    @Override
    public Class<?> getBootstrappedElement() {
        return this.clazz;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    public IBootstrapper getBootstrapper() {
        return this.bootstrapper;
    }

    @Override
    public String getSimpleName() {
        return "Class";
    }

    @Override
    public int getWeight() {
        if (this.isProvided()) {
            return Integer.MAX_VALUE - 50500;
        }

        return IBootstrappedElement.super.getWeight();
    }

    // TODO: maybe refactor these two methods to be a little less copy and pasted
    @Override
    public void execute() {
        LOGGER.debug("Executing transformation blueprint for " + this.clazz.getName());
        this.transformingBlueprint.transform();

        IBootstrappedElement.super.execute();
    }

    @Override
    public Consumer<IPreparedAnnotationProcessor> executeClass() {
        return (processor) -> processor.executeClass(this.getBootstrappedElement());
    }

    @Override
    public Consumer<IWinryPreparedAnnotationProcessor> transformClass() {
        return (processor) -> processor.transformClass(this, this.transformingBlueprint);
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
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public List<BootstrappedField> getFields() {
        return Collections.unmodifiableList(this.fields);
    }

    public List<BootstrappedMethod> getMethods() {
        return Collections.unmodifiableList(this.methods);
    }

    public Class<?> getWrappedClass() {
        return this.wrappedClazz;
    }

    public ITransformingBlueprint getTransformingBlueprint() {
        return this.transformingBlueprint;
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
