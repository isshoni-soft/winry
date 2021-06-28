package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.entity.bytebuddy.ClassTransformingPlan;
import tv.isshoni.winry.entity.bytebuddy.FieldTransformingPlan;
import tv.isshoni.winry.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.entity.bytebuddy.ITransformingPlan;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ClassTransformingBlueprint implements ITransformingBlueprint {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    private static final WinryLogger LOGGER = WinryLogger.create("ClassTransformingPlan");

    private final BootstrappedClass bootstrappedClass;

    private final IElementBootstrapper elementBootstrapper;

    private final Map<Method, List<ITransformingPlan<Method, BootstrappedMethod>>> methodTransformers;

    private final Map<Field, List<ITransformingPlan<Field, BootstrappedField>>> fieldTransformers;

    private final List<ITransformingPlan<Class<?>, BootstrappedClass>> classTransformers;

    public ClassTransformingBlueprint(BootstrappedClass bootstrappedClass) {
        this.bootstrappedClass = bootstrappedClass;
        this.elementBootstrapper = bootstrappedClass.getBootstrapper().getElementBootstrapper();
        this.methodTransformers = new HashMap<>();
        this.fieldTransformers = new HashMap<>();
        this.classTransformers = new LinkedList<>();
    }

    @Override
    public void transform() {
        DynamicType.Builder<?> builder = BYTE_BUDDY.subclass(this.bootstrappedClass.getBootstrappedElement())
                .defineMethod("isWinryWrapped", Boolean.TYPE, Modifier.PUBLIC | Modifier.STATIC)
                .intercept(FixedValue.value(true));

        builder = executeTransformation(builder, this.bootstrappedClass.getBootstrappedElement(), this.bootstrappedClass, this.classTransformers);

        for (Map.Entry<Field, List<ITransformingPlan<Field, BootstrappedField>>> entry : this.fieldTransformers.entrySet()) {
            Field field = entry.getKey();

            builder = executeTransformation(builder, field, this.elementBootstrapper.getBootstrappedField(field), entry.getValue());
        }

        for (Map.Entry<Method, List<ITransformingPlan<Method, BootstrappedMethod>>> entry : this.methodTransformers.entrySet()) {
            Method method = entry.getKey();

            builder = executeTransformation(builder, method, this.elementBootstrapper.getBootstrappedMethod(method), entry.getValue());
        }

        this.bootstrappedClass.setWrappedClass(builder
                .name("WinryWrapped" + this.bootstrappedClass.getBootstrappedElement().getSimpleName())
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded());
    }

    @Override
    public void registerAdvancedClassTransformation(ClassTransformingPlan transformingPlan) {
        if (this.classTransformers.contains(transformingPlan)) {
            LOGGER.warning("Registering more than one transformer to one element, this can lead to unexpected behavior!");
        }

        this.classTransformers.add(transformingPlan);
    }

    @Override
    public void registerAdvancedMethodTransformation(Method method, MethodTransformingPlan transformingPlan) {
        register(method, transformingPlan, this.methodTransformers);
    }

    @Override
    public void registerAdvancedFieldTransformation(Field field, FieldTransformingPlan transformingPlan) {
        register(field, transformingPlan, this.fieldTransformers);
    }

    @Override
    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

    @Override
    public Map<Method, List<ITransformingPlan<Method, BootstrappedMethod>>> getMethodTransformers() {
        return Streams.to(this.methodTransformers.entrySet())
                .mapToPair(Map.Entry::getKey, Map.Entry::getValue)
                .mapSecond(Collections::unmodifiableList)
                .toUnmodifiableMap();
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>> DynamicType.Builder<?> executeTransformation(DynamicType.Builder<?> builder, E element, B bootstrapped, List<ITransformingPlan<E, B>> list) {
        for (ITransformingPlan<E, B> transformingPlan : list) {
            builder = transformingPlan.transform(element, bootstrapped, builder);
        }

        return builder;
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>> void register(E element, ITransformingPlan<E, B> transformer, Map<E, List<ITransformingPlan<E, B>>> map) {
        if (map.containsKey(element)) {
            LOGGER.warning("Registering more than one transformer to one element, this can lead to unexpected behavior!");
        }

        map.compute(element, compute(transformer));
    }

    private <T, E, U extends List<E>> BiFunction<T, U, U> compute(E val) {
        return (k, v) -> {
            if (v == null) {
                v = (U) new LinkedList<E>();
            }

            v.add(val);

            return v;
        };
    }
}
