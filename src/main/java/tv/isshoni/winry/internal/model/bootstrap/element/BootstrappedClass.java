package tv.isshoni.winry.internal.model.bootstrap.element;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.bytebuddy.ITransformingBlueprint;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@Deprecated
public class BootstrappedClass implements IBootstrappedElement<Class<?>>, IContextual {

    private final AraragiLogger LOGGER;

    private final IBootstrapper bootstrapper;

    private final IWinryContext context;

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
    private boolean built = false;

    public BootstrappedClass(Class<?> clazz, IBootstrapper bootstrapper, IWinryContext context) {
        this.context = context;
        this.LOGGER = bootstrapper.getContext().getLoggerFactory().createLogger("BootstrappedClass");
        this.clazz = clazz;
        this.bootstrapper = bootstrapper;
        this.modifiers = ReflectedModifier.getModifiers(clazz);
        this.transformingBlueprint = bootstrapper.getContext().getElementBootstrapper().supplyTransformingBlueprint(this);
        this.annotations = new LinkedList<>();
        this.fields = new LinkedList<>();
        this.methods = new LinkedList<>();

        build();
    }

    public void build() {
        if (!this.built) {
            this.built = true;
            LOGGER.debug("Building: " + getBootstrappedElement().getName());
        }

        compileAnnotations();

        if (this.built) {
            LOGGER.debug("Rebuilding: " + getBootstrappedElement().getName());
        }

        this.fields.clear();
        this.methods.clear();

        Streams.to(getBootstrappedElement().getDeclaredFields())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(this.context.getElementBootstrapper()::bootstrap)
                .forEach(this.fields::add);
        LOGGER.debug("Discovered " + getFields().size() + " fields");

        Streams.to(getBootstrappedElement().getDeclaredMethods())
                .filter(this.context.getAnnotationManager()::hasManagedAnnotation)
                .map(this.context.getElementBootstrapper()::bootstrap)
                .forEach(this.methods::add);
        LOGGER.debug("Discovered " + getMethods().size() + " methods");
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

    public Class<?> findClass() {
        return (hasWrappedClass() ? getWrappedClass() : this.clazz);
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
    public void compileAnnotations() {
        IWinryAnnotationManager annotationManager = this.bootstrapper.getContext().getAnnotationManager();

        List<Annotation> annotations = annotationManager.getManagedAnnotationsOn(this.clazz);

        this.annotations.clear();
        this.annotations.addAll(annotations);

        if (annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.clazz.getSimpleName() + " has conflicting annotations! " + annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    @Override
    public int getWeight() {
        if (this.isProvided()) {
            return Integer.MAX_VALUE - 50500;
        }

        return IBootstrappedElement.super.getWeight();
    }

    @Override
    public void transform() {
        IBootstrappedElement.super.transform();

        getFields().forEach(IBootstrappedElement::transform);
        getMethods().forEach(IBootstrappedElement::transform);

        LOGGER.debug("Executing transformation blueprint for " + this.clazz.getName());
        this.transformingBlueprint.transform();
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

    @Override
    public int hashCode() {
        return this.getBootstrappedElement().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BootstrappedClass oc)) {
            return false;
        }

        return oc.clazz.equals(this.clazz) && oc.context.equals(this.context);
    }
}
