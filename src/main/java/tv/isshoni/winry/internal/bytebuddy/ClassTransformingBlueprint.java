package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.implementation.MethodCall;
import tv.isshoni.araragi.logging.AraragiLogger;
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
import tv.isshoni.winry.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.entity.bytebuddy.MethodTransformingPlan;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.SuperMethodCall;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class ClassTransformingBlueprint implements ITransformingBlueprint {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    private final AraragiLogger LOGGER;

    private final BootstrappedClass bootstrappedClass;

    private final IElementBootstrapper elementBootstrapper;

    private final Map<Field, ITransformingPlan<Field, BootstrappedField>> fieldTransformers;

    private final Map<Method, ITransformingPlan<Method, BootstrappedMethod>> methodTransformers;

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

        DynamicType.Builder<?> builder = BYTE_BUDDY.subclass(this.bootstrappedClass.getBootstrappedElement(), ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineMethod("isWinryWrapped", Boolean.TYPE, Modifier.PUBLIC | Modifier.STATIC)
                .intercept(FixedValue.value(true));

        for (Constructor<?> constructor : this.bootstrappedClass.getBootstrappedElement().getDeclaredConstructors()) {
            DynamicType.Builder.MethodDefinition.ParameterDefinition<?> defineParameters = builder.defineConstructor(Modifier.PUBLIC);

            for (int x = 0; x < constructor.getParameters().length; x++) {
                Parameter current = constructor.getParameters()[x];

                defineParameters = defineParameters.withParameter(current.getType(), current.getName())
                        .annotateParameter(current.getAnnotations());

                LOGGER.debug("Attaching " + Arrays.toString(current.getAnnotations()) + " to " + current.getName() + " in " + constructor);
            }

            builder = defineParameters.intercept(SuperMethodCall.INSTANCE);
        }

        builder = executeTransformation(builder, this.bootstrappedClass.getBootstrappedElement(), this.bootstrappedClass, this.classTransformer);

        for (Map.Entry<Field, ITransformingPlan<Field, BootstrappedField>> entry : this.fieldTransformers.entrySet()) {
            Field field = entry.getKey();

            builder = executeTransformation(builder, field, this.elementBootstrapper.getBootstrappedField(field), entry.getValue());
        }

        for (Map.Entry<Method, ITransformingPlan<Method, BootstrappedMethod>> entry : this.methodTransformers.entrySet()) {
            Method method = entry.getKey();

            builder = executeTransformation(builder, method, this.elementBootstrapper.getBootstrappedMethod(method), entry.getValue());
        }

        List<Method> portedMethods = Streams.to(this.bootstrappedClass.getBootstrappedElement().getDeclaredMethods())
                .filterInverted(this.methodTransformers::containsKey)
                .toList();

        for (Method method : portedMethods) {
            builder = WinryMethodTransformer.replicateMethod(method, builder)
                    .intercept(MethodCall.invoke(method).onSuper().withAllArguments());
        }

        this.bootstrappedClass.setWrappedClass(builder
                .name("WinryWrapped" + this.bootstrappedClass.getBootstrappedElement().getSimpleName())
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded());
    }

    @Override
    public void registerSimpleMethodDelegator(Method method, int weight, MethodDelegator delegator) {
        WinryMethodTransformer transformer;

        if (this.methodTransformers.containsKey(method)) {
            if (!(this.methodTransformers.get(method) instanceof WinryMethodTransformer)) {
                LOGGER.error("Cannot register simple method delegator to method that does not use WinryMethodTransformer!");
                return;
            }

            transformer = (WinryMethodTransformer) this.methodTransformers.get(method);
        } else {
            transformer = new WinryMethodTransformer();

            this.methodTransformers.put(method, transformer);
        }

        transformer.addDelegator(delegator, weight);
    }

    @Override
    public void registerAdvancedClassTransformation(ClassTransformingPlan transformingPlan) {
        if (Objects.nonNull(this.classTransformer)) {
            LOGGER.warn("Overwriting default Winry class transformer!");
        }

        this.classTransformer = transformingPlan;
    }

    @Override
    public void registerAdvancedMethodTransformation(Method method, MethodTransformingPlan transformingPlan) {
        LOGGER.warn("Overwriting default Winry method transformer!");

        register(method, transformingPlan, this.methodTransformers);
    }

    @Override
    public void registerAdvancedFieldTransformation(Field field, FieldTransformingPlan transformingPlan) {
        LOGGER.warn("Overwriting default Winry field transformer!");

        register(field, transformingPlan, this.fieldTransformers);
    }

    @Override
    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

    @Override
    public Map<Method, ITransformingPlan<Method, BootstrappedMethod>> getMethodTransformers() {
        return Collections.unmodifiableMap(this.methodTransformers);
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>> DynamicType.Builder<?> executeTransformation(DynamicType.Builder<?> builder, E element, B bootstrapped, ITransformingPlan<E, B> plan) {
        if (Objects.isNull(plan)) {
            return builder;
        }

        return plan.transform(element, bootstrapped, builder);
    }

    private <E extends AnnotatedElement, B extends IBootstrappedElement<E>> void register(E element, ITransformingPlan<E, B> transformer, Map<E, ITransformingPlan<E, B>> map) {
        if (map.containsKey(element)) {
            LOGGER.warn("Registering more than one transformer to one element, this can lead to unexpected behavior!");
        }

        map.putIfAbsent(element, transformer);
    }
}