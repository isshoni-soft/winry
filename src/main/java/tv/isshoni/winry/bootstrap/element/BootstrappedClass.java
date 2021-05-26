package tv.isshoni.winry.bootstrap.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.bytebuddy.ByteBuddyUtil;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.util.Arrays;
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
    private final Class<?> wrappedClazz;

    private Object object;

    private final A annotation;

    private final List<BootstrappedField<?>> fields;

    private final List<BootstrappedMethod> methods;

    private final Set<ReflectedModifier> modifiers;

    private boolean provided = false;

    public BootstrappedClass(Class<?> clazz, A annotation) {
        this.clazz = clazz;
        this.annotation = annotation;
        this.modifiers = ReflectedModifier.getModifiers(clazz);
        this.fields = new LinkedList<>();
        this.methods = new LinkedList<>();
        this.wrappedClazz = ByteBuddyUtil.wrapClass(this)
                .name("WinryWrapped" + this.clazz.getSimpleName())
                .make()
                .load(BootstrappedClass.class.getClassLoader())
                .getLoaded();
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
            return 10000;
        }

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

        LOGGER.info("Produced wrapped class: " + this.wrappedClazz.getName());

        Arrays.stream(this.wrappedClazz.getDeclaredMethods()).forEach(m -> LOGGER.info(m.toString()));
    }

    public Object getObject() {
        return this.object;
    }

    public boolean isProvided() {
        return this.provided;
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

//    public Class<?> getWrappedClazz() {
//        return this.wrappedClazz;
//    }

    public boolean hasObject() {
        return Objects.nonNull(this.object);
    }

    @Override
    public String toString() {
        return "BootstrappedClass[class=" + this.clazz.getName() + ",annotation=" + this.annotation.annotationType().getName() + ",bootstrapped=" + this.hasObject() + ",weight=" + this.getWeight() + "]";
    }
}
