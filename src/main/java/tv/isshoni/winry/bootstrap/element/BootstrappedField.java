package tv.isshoni.winry.bootstrap.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectedModifier;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class BootstrappedField<A extends Annotation> implements IBootstrappedElement<A, Field> {

    private static final WinryLogger LOGGER = WinryLogger.create("BootstrapField", 11);

    private static final Map<Class<? extends Annotation>, BiConsumer<Map<Class<?>, Object>, BootstrappedField<?>>> ANNOTATION_PROCEDURE = new HashMap<>();

    static {
        registerAnnotationProcedure(Logger.class, (provided, field) -> {
            if (!field.field.getType().equals(WinryLogger.class)) {
                LOGGER.severe(field.getDisplay() + " is not of type WinryLogger, skipping...");
                return;
            }

            if (field.modifiers.contains(ReflectedModifier.FINAL)) {
                LOGGER.severe(field.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
                return;
            }

            Logger annotation = (Logger) field.annotation;

            WinryLogger logger = ReflectionManager.executeMethod(WinryLogger.class, null, "create", annotation.value(), annotation.indent());

            LOGGER.info("Injecting: " + logger);
            ReflectionManager.injectField(field, logger);
        });
    }

    public static void registerAnnotationProcedure(Class<? extends Annotation> annotation, BiConsumer<Map<Class<?>, Object>, BootstrappedField<?>> consumer) {
        if (ANNOTATION_PROCEDURE.containsKey(annotation)) {
            throw new IllegalStateException(annotation.getName() + " already has a procedure registered!");
        }

        ANNOTATION_PROCEDURE.put(annotation, consumer);
    }

    private final Field field;

    private final Set<ReflectedModifier> modifiers;

    private final A annotation;

    private final BootstrappedClass<?> target;

    public BootstrappedField(Field field, A annotation, BootstrappedClass<?> target) {
        this.field = field;
        this.annotation = annotation;
        this.target = target;
        this.modifiers = ReflectedModifier.getModifiers(field);
    }

    @Override
    public A getAnnotation() {
        return this.annotation;
    }

    @Override
    public Field getBootstrappedElement() {
        return this.field;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifiers);
    }

    public BootstrappedClass<?> getTarget() {
        return this.target;
    }

    @Override
    public int getWeight() {
        if (this.annotation instanceof Logger) {
            return 7;
        }

        if (this.target.isProvided()) {
            return 8;
        }

        if (this.target.getAnnotation() instanceof Bootstrap) {
            return 5;
        }

        if (this.target.getAnnotation() instanceof Injected) {
            Injected injected = (Injected) this.target.getAnnotation();

            if (injected.weight() == Injected.DEFAULT_WEIGHT) {
                return injected.value().getWeight();
            }

            return injected.weight();
        }

        return 3;
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        if (ANNOTATION_PROCEDURE.containsKey(this.annotation.annotationType())) {
            LOGGER.info("Executing Procedure: " + this.annotation.annotationType());
            ANNOTATION_PROCEDURE.get(this.annotation.annotationType()).accept(provided, this);
            return;
        }

        if (this.target == null) {
            LOGGER.severe("Unable to inject class for " + getDisplay() + ", can't find the target!");
            return;
        }

        LOGGER.info("Injecting: " + this.target);
        ReflectionManager.injectField(this);
    }

    public String getDisplay() {
        return ReflectedModifier.toString(this.field) + " " + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName();
    }

    @Override
    public String toString() {
        return "BootstrappedField[field=" + this.field.getName() + ",target=" + (this.target != null ? this.target.getBootstrappedElement().getName() : "null") + ",weight=" + this.getWeight() + "]";
    }
}
