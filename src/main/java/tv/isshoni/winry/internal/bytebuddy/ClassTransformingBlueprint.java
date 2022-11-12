package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.SuperMethodCall;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.internal.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.internal.entity.bytebuddy.ClassTransformingPlan;
import tv.isshoni.winry.internal.entity.bytebuddy.FieldTransformingPlan;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingPlan;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassTransformingBlueprint implements ITransformingBlueprint {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    private final AraragiLogger LOGGER;

    private final BootstrappedClass bootstrappedClass;

    private final IElementBootstrapper elementBootstrapper;

    private final Map<Field, FieldTransformingPlan> fieldTransformers;

    private final Map<Method, MethodTransformingPlan> methodTransformers;

    private ITransformingPlan<Class<?>, BootstrappedClass> classTransformer;

    public ClassTransformingBlueprint(BootstrappedClass bootstrappedClass) {
        LOGGER = bootstrappedClass.getBootstrapper().getContext().getLoggerFactory().createLogger("ClassTransformingBlueprint");
        this.bootstrappedClass = bootstrappedClass;
        this.elementBootstrapper = bootstrappedClass.getBootstrapper().getContext().getElementBootstrapper();
        this.methodTransformers = new HashMap<>();
        this.fieldTransformers = new HashMap<>();
    }

    @Override
    public void transform() {
        LOGGER.debug("Transforming: " + this.bootstrappedClass.getDisplay());

        String packageName = this.bootstrappedClass.getBootstrappedElement().getCanonicalName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));

        LOGGER.debug("Found package: " + packageName);

        DynamicType.Builder<?> builder = BYTE_BUDDY.subclass(this.bootstrappedClass.getBootstrappedElement(), ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineMethod("isWinryWrapped", Boolean.TYPE, Modifier.PUBLIC | Modifier.STATIC)
                .intercept(FixedValue.value(true));

        for (Constructor<?> constructor : this.bootstrappedClass.getBootstrappedElement().getDeclaredConstructors()) {
            DynamicType.Builder.MethodDefinition.ParameterDefinition<?> defineParameters = builder.defineConstructor(Modifier.PUBLIC);

            for (Parameter current : constructor.getParameters()) {
                defineParameters = defineParameters.withParameter(current.getParameterizedType(), current.getName())
                        .annotateParameter(current.getAnnotations());

                LOGGER.debug("Attaching " + Arrays.toString(current.getAnnotations()) + " to " + current.getName() + " in " + constructor);
            }

            builder = defineParameters.intercept(SuperMethodCall.INSTANCE);
        }

        builder = executeTransformation(builder, this.bootstrappedClass.getBootstrappedElement(), this.bootstrappedClass, this.classTransformer);

        for (Map.Entry<Field, FieldTransformingPlan> entry : this.fieldTransformers.entrySet()) {
            Field field = entry.getKey();

            builder = executeTransformation(builder, field, this.elementBootstrapper.getBootstrappedField(field), entry.getValue());
        }

        for (Map.Entry<Method, MethodTransformingPlan> entry : this.methodTransformers.entrySet()) {
            Method method = entry.getKey();

            builder = executeTransformation(builder, method, this.elementBootstrapper.getBootstrappedMethod(method), entry.getValue());
        }

        this.bootstrappedClass.setWrappedClass(builder
                .name(packageName + ".WinryWrapped" + this.bootstrappedClass.getBootstrappedElement().getSimpleName())
                .make()
                .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded());
    }

    @Override
    public void setClassTransformingPlan(ClassTransformingPlan transformingPlan) {
        if (Objects.nonNull(this.classTransformer)) {
            LOGGER.warn("Overwriting default Winry class transformer!");
        }

        this.classTransformer = transformingPlan;
    }

    @Override
    public void setMethodTransformingPlan(Method method, MethodTransformingPlan transformingPlan) {
        LOGGER.warn("Overwriting transforming plan for method: " + method.getName());

        register(method, transformingPlan, this.methodTransformers);
    }

    @Override
    public void setFieldTransformingPlan(Field field, FieldTransformingPlan transformingPlan) {
        LOGGER.warn("Overwriting transforming plan for field: " + field.getName());

        register(field, transformingPlan, this.fieldTransformers);
    }

    @Override
    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

    @Override
    public MethodTransformingPlan getMethodTransformingPlan(Method method) {
        MethodTransformingPlan transformer;

        if (!this.methodTransformers.containsKey(method)) {
            transformer = supplyDefaultMethodTransformingPlan(this.elementBootstrapper.getBootstrappedMethod(method));
            this.methodTransformers.put(method, transformer);
        } else {
            transformer = this.methodTransformers.get(method);
        }

        return transformer;
    }

    @Override
    public FieldTransformingPlan getFieldTransformingPlan(Field field) {
        FieldTransformingPlan transformer;

        if (!this.fieldTransformers.containsKey(field)) {
            transformer = supplyDefaultFieldTransformingPlan();
            this.fieldTransformers.put(field, transformer);
        } else {
            transformer = this.fieldTransformers.get(field);
        }

        return transformer;
    }

    @Override
    public MethodTransformingPlan supplyDefaultMethodTransformingPlan(BootstrappedMethod method) {
        return new WinryMethodTransformer(method);
    }

    @Override
    public FieldTransformingPlan supplyDefaultFieldTransformingPlan() {
        return new WinryFieldTransformer();
    }

    @Override
    public Map<Method, ITransformingPlan<Method, BootstrappedMethod>> getMethodTransformers() {
        return Collections.unmodifiableMap(this.methodTransformers);
    }

    @Override
    public Map<Field, ITransformingPlan<Field, BootstrappedField>> getFieldTransformers() {
        return Collections.unmodifiableMap(this.fieldTransformers);
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>> DynamicType.Builder<?> executeTransformation(DynamicType.Builder<?> builder, E element, B bootstrapped, ITransformingPlan<E, B> plan) {
        if (Objects.isNull(plan)) {
            return builder;
        }

        return plan.transform(element, bootstrapped, builder);
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>, TP extends ITransformingPlan<E, B>> void register(E element, TP transformer, Map<E, TP> map) {
        map.put(element, transformer);
    }
}
