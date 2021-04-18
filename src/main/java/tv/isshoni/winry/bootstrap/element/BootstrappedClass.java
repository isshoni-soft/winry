package tv.isshoni.winry.bootstrap.element;

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

public class BootstrappedClass<A extends Annotation> implements IBootstrappedElement<A, Class<?>> {

    private static final WinryLogger LOGGER = WinryLogger.create("BootstrappedClass", 8);

    private static final Map<Class<? extends Annotation>, Integer> ANNOTATION_WEIGHTS = new HashMap<>();

    static {
        registerBootstrapClassAnnotationWeight(Bootstrap.class, Integer.MAX_VALUE);
    }

    public static void registerBootstrapClassAnnotationWeight(Class<? extends Annotation> annotation, int weight) {
        if (ANNOTATION_WEIGHTS.containsKey(annotation)) {
            throw new IllegalStateException(annotation.getName() + " is already registered!");
        }

        ANNOTATION_WEIGHTS.put(annotation, weight);
    }

    private final Class<?> clazz;

    private Object object;

    private final A annotation;

    private final List<BootstrappedField<?>> fields;

    private final List<BootstrappedMethod> methods;

    private final Set<ReflectedModifier> modifiers;

    public BootstrappedClass(Class<?> clazz, A annotation) {
        this.clazz = clazz;
        this.annotation = annotation;
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
        return ANNOTATION_WEIGHTS.getOrDefault(this.annotation.annotationType(), 10);
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        if (provided.containsKey(this.clazz)) {
            LOGGER.info("Class: " + this.clazz.getName() + " is provided.");
            this.object = provided.get(this.clazz);
        } else {
            LOGGER.info("Class: new " + this.clazz.getName() + "()");
            this.object = ReflectionManager.construct(this);
        }

        LOGGER.info("Registered to class registry");
        ReflectionManager.registerClass(this);
    }

    public Object getObject() {
        return this.object;
    }

    @Override
    public A getAnnotation() {
        return this.annotation;
    }

    public List<BootstrappedField<?>> getFields() {
        return ImmutableList.copyOf(this.fields);
    }

    public List<BootstrappedMethod> getMethods() {
        return ImmutableList.copyOf(this.methods);
    }

    public boolean hasObject() {
        return Objects.nonNull(this.object);
    }

    @Override
    public String toString() {
        return "BootstrappedClass[class=" + this.clazz.getName() + ",annotation=" + this.annotation.annotationType().getName() + ",bootstrapped=" + this.hasObject() + ",weight=" + this.getWeight() + "]";
    }
}
