package tv.isshoni.winry.entity.element;

import com.google.common.collect.ImmutableSet;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.entity.annotation.PreparedAnnotationProcessor;
import tv.isshoni.winry.reflection.ReflectedModifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BootstrappedField implements IBootstrappedElement<Field> {

//    private static final Map<Class<? extends Annotation>, BiConsumer<Map<Class<?>, Object>, BootstrappedField>> ANNOTATION_PROCEDURE = new HashMap<>();
//
//    static {
//        registerAnnotationProcedure(Logger.class, (provided, field) -> {
//            if (!field.field.getType().equals(WinryLogger.class)) {
//                LOGGER.severe(field.getDisplay() + " is not of type WinryLogger, skipping...");
//                return;
//            }
//
//            if (field.modifiers.contains(ReflectedModifier.FINAL)) {
//                LOGGER.severe(field.getDisplay() + " has modifier final, Winry is currently unable to inject into final fields, skipping...");
//                return;
//            }
//
//            Logger annotation = (Logger) field.annotations;
//
//            WinryLogger logger = ReflectionManager.executeMethod(WinryLogger.class, null, "create", annotation.value(), annotation.indent());
//
//            LOGGER.info("Injecting: " + logger);
//            ReflectionManager.injectField(field, logger);
//        });
//    }
//
//    public static void registerAnnotationProcedure(Class<? extends Annotation> annotation, BiConsumer<Map<Class<?>, Object>, BootstrappedField> consumer) {
//        if (ANNOTATION_PROCEDURE.containsKey(annotation)) {
//            throw new IllegalStateException(annotation.getName() + " already has a procedure registered!");
//        }
//
//        ANNOTATION_PROCEDURE.put(annotation, consumer);
//    }

    private final AnnotationManager annotationManager;

    private final Field field;

    private final Set<ReflectedModifier> modifiers;

    private final Collection<Annotation> annotations;

    private final BootstrappedClass target;

    private boolean injected = false;

    public BootstrappedField(Field field, BootstrappedClass target, AnnotationManager annotationManager) {
        this.field = field;
        this.annotationManager = annotationManager;
        this.target = target;
        this.modifiers = ReflectedModifier.getModifiers(field);
        this.annotations = this.annotationManager.getManagedAnnotationsOn(field);

        if (this.annotationManager.hasConflictingAnnotations(this.annotations)) {
            throw new IllegalStateException(this.field.getName() + " has conflicting annotations! " + this.annotationManager.getConflictingAnnotations(this.annotations));
        }
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public Field getBootstrappedElement() {
        return this.field;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifiers);
    }

    @Override
    public AnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

    public BootstrappedClass getTarget() {
        return this.target;
    }

    public boolean isInjected() {
        return this.injected;
    }

    @Override
    public int getWeight() {
        if (this.target != null && this.target.isProvided()) {
            return 8; // TODO: Revisit these values
        }

        return IBootstrappedElement.super.getWeight();
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        for (PreparedAnnotationProcessor processor : this.annotationManager.toExecutionList(this.annotations)) {
            processor.executeField(this, provided);
        }
    }

    @Override
    public String getDisplay() {
        return ReflectedModifier.toString(this.field) + " " + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName();
    }

    @Override
    public String toString() {
        return "BootstrappedField[field=" + this.field.getName() + ",target=" + (this.target != null ? this.target.getBootstrappedElement().getName() : "null") + ",weight=" + this.getWeight() + "]";
    }
}
