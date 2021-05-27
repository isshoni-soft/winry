package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BootstrappedClass implements IBootstrappedElement<Class<?>> {

    private static final WinryLogger LOGGER = WinryLogger.create("BootstrappedClass", 8);

    @Deprecated
    private static final Map<Class<? extends Annotation>, Integer> ANNOTATION_WEIGHTS = new HashMap<>();

    static {
        registerBootstrapClassAnnotationWeight(Bootstrap.class, Integer.MAX_VALUE);
    }

    @Deprecated
    public static void registerBootstrapClassAnnotationWeight(Class<? extends Annotation> annotation, int weight) {
        if (ANNOTATION_WEIGHTS.containsKey(annotation)) {
            throw new IllegalStateException(annotation.getName() + " is already registered!");
        }

        ANNOTATION_WEIGHTS.put(annotation, weight);
    }

    private final Class<?> clazz;
    private Class<?> wrappedClazz;

    private Object object;

    private final Collection<Annotation> annotations;

    private final List<BootstrappedField> fields;

    private final List<BootstrappedMethod> methods;

    private final Set<ReflectedModifier> modifiers;

    private boolean provided = false;

    public BootstrappedClass(Class<?> clazz, Collection<Annotation> annotations) {
        this.clazz = clazz;
        this.annotations = annotations;
        this.modifiers = ReflectedModifier.getModifiers(clazz);
        this.fields = new LinkedList<>();
        this.methods = new LinkedList<>();
    }

    public void addField(BootstrappedField<?> field) {
        this.fields.add(field);
    }

    public void addField(Collection<BootstrappedField<?>> fields) {
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

    @Override
    public Class<?> getBootstrappedElement() {
        return this.clazz;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifiers);
    }

    @Override
    public int getWeight() {
        if (this.isProvided()) {
            return Integer.MAX_VALUE - 500;
        }

        // TODO: UPDATE ME TO USE @WEIGHT
        int result = 0;

        for (Annotation annotation : this.annotations) {
            result += ANNOTATION_WEIGHTS.getOrDefault(annotation.annotationType(), 10);
        }

        return result;
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        if (hasWrappedClass()) {
            LOGGER.info("Produced wrapped class: " + this.wrappedClazz.getName());
        }

        if (provided.containsKey(this.clazz)) {
            LOGGER.info("Class: " + this.clazz.getName() + " is provided.");
            this.object = provided.get(this.clazz);
        } else if (hasWrappedClass()) {
            LOGGER.info("Class: new " + this.wrappedClazz.getName() + "()");
            this.object = ReflectionManager.construct(this.wrappedClazz);
        } else {
            LOGGER.info("Class: new " + this.clazz.getName() + "()");
            this.object = ReflectionManager.construct(this.clazz);
        }

        LOGGER.info("Registered to class registry");
        ReflectionManager.registerClass(this);
    }

    public Object getObject() {
        return this.object;
    }

    public boolean isProvided() {
        return this.provided;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    public List<BootstrappedField<?>> getFields() {
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
        return "BootstrappedClass[class=" + this.clazz.getName() + ",annotation=" + this.annotations + ",bootstrapped=" + this.hasObject() + ",weight=" + this.getWeight() + "]";
    }
}
