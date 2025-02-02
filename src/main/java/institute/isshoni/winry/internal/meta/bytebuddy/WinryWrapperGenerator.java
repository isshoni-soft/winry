package institute.isshoni.winry.internal.meta.bytebuddy;

import institute.isshoni.araragi.logging.AraragiLogger;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.SuperMethodCall;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.meta.IAnnotatedField;
import institute.isshoni.winry.api.meta.IAnnotatedMethod;
import institute.isshoni.winry.api.meta.IWinryTransformed;
import institute.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IClassTransformer;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IFieldTransformer;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IMethodDelegator;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IMethodTransformer;
import institute.isshoni.winry.internal.model.meta.bytebuddy.ITransformer;
import institute.isshoni.winry.internal.model.meta.bytebuddy.IWrapperGenerator;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WinryWrapperGenerator implements IWrapperGenerator {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    private final IWinryContext context;

    private final AraragiLogger logger;

    private final IAnnotatedClass toWrap;

    private IClassTransformer classTransformer;

    private final Map<IAnnotatedMethod, IMethodTransformer> methodTransformers;

    private final Map<IAnnotatedField, IFieldTransformer> fieldTransformers;

    public WinryWrapperGenerator(IWinryContext context, IAnnotatedClass toWrap) {
        this.context = context;
        this.toWrap = toWrap;
        this.logger = this.context.createLogger("WrapperGenerator");
        this.methodTransformers = new HashMap<>();
        this.fieldTransformers = new HashMap<>();
    }

    @Override
    public Class<?> generate() {
        logger.debug("Transforming: " + this.toWrap.getDisplay());

        String packageName = this.toWrap.getElement().getCanonicalName();
        int lastDotIndex = packageName.lastIndexOf('.');

        if (lastDotIndex == -1) {
            packageName = "";
        } else {
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        }

        logger.debug("-> Found package: " + packageName);

        if (!packageName.isEmpty()) {
            packageName += '.';
        }

        DynamicType.Builder<?> builder = BYTE_BUDDY.subclass(this.toWrap.getElement(), ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .annotateType(this.toWrap.getElement().getAnnotations())
                .implement(IWinryTransformed.class)
                .defineMethod("isWinryWrapped", Boolean.TYPE, Modifier.PUBLIC | Modifier.STATIC)
                .intercept(FixedValue.value(true));

        logger.debug("-> Subclassing: " + this.toWrap.getElement());

        for (Constructor<?> constructor : this.toWrap.getElement().getDeclaredConstructors()) {
            DynamicType.Builder.MethodDefinition.ParameterDefinition<?> defineParameters = builder.defineConstructor(Modifier.PUBLIC);

            for (Parameter current : constructor.getParameters()) {
                defineParameters = defineParameters.withParameter(current.getParameterizedType(), current.getName())
                        .annotateParameter(current.getAnnotations());

                logger.debug("Attaching " + Arrays.toString(current.getAnnotations()) + " to " + current.getName() + " in " + constructor);
            }

            builder = defineParameters.intercept(SuperMethodCall.INSTANCE);
        }

        builder = executeTransformation(builder, this.toWrap, this.classTransformer);

        for (Map.Entry<IAnnotatedField, IFieldTransformer> entry : this.fieldTransformers.entrySet()) {
            builder = executeTransformation(builder, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<IAnnotatedMethod, IMethodTransformer> entry : this.methodTransformers.entrySet()) {
            logger.debug("Executing method transformer: ${0}", entry.getKey().getElement().getName());
            builder = executeTransformation(builder, entry.getKey(), entry.getValue());
        }

        return builder
                .name(packageName + this.toWrap.getElement().getSimpleName() + "$$Winry$" + this.context.getContextId())
                .make()
                .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }

    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public void setClassTransformer(IClassTransformer classTransformer) {
        this.classTransformer = classTransformer;
    }

    @Override
    public void setMethodTransformer(IAnnotatedMethod method, IMethodTransformer transformer) {
        setMethodTransformer(method, transformer, false);
    }

    @Override
    public void setMethodTransformer(IAnnotatedMethod method, IMethodTransformer transformer, boolean force) {
        if (!force && this.methodTransformers.containsKey(method)) {
            logger.warn("Cannot set multiple method transformers for one method!");
            return;
        }

        this.methodTransformers.put(method, transformer);
    }

    @Override
    public void delegateMethod(IAnnotatedMethod method, int weight, IMethodDelegator delegator) {
        logger.debug("Registered delegation for method: " + method.getElement().getName());
        WinryMethodDelegator methodDelegator = new WinryMethodDelegator(this.context);

        if (this.methodTransformers.containsKey(method)) {
            IMethodTransformer transformer = this.methodTransformers.get(method);

            if (transformer instanceof WinryMethodDelegator) {
                methodDelegator = (WinryMethodDelegator) transformer;
            } else {
                throw new IllegalStateException("Cannot register method delegator to method with pre-existing non-delegator transformer type!");
            }
        } else {
            setMethodTransformer(method, methodDelegator);
        }

        methodDelegator.registerDelegator(delegator, weight);
    }

    @Override
    public boolean hasTransformer(IAnnotatedMethod method) {
        return this.methodTransformers.containsKey(method) && this.methodTransformers.get(method) != null;
    }

    @Override
    public IAnnotatedClass toWrap() {
        return this.toWrap;
    }

    @Override
    public IClassTransformer getClassTransformer() {
        return this.classTransformer;
    }

    private <E extends AnnotatedElement, M extends IAnnotatedMeta<E>> DynamicType.Builder<?> executeTransformation(DynamicType.Builder<?> builder, M meta, ITransformer<E, M> plan) {
        if (Objects.isNull(plan)) {
            return builder;
        }

        return plan.transform(meta.getElement(), meta, builder);
    }
}
