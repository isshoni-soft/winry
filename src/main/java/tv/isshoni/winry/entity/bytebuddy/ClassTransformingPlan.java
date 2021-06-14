package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassTransformingPlan implements ITransformingPlan<Class<?>, BootstrappedClass> {

    private static final WinryLogger LOGGER = WinryLogger.create("ClassTransformingPlan");

    private final BootstrappedClass bootstrappedClass;

    private final Map<Method, List<MethodTransformingPlan>> methods;

    public ClassTransformingPlan(BootstrappedClass bootstrappedClass) {
        this.bootstrappedClass = bootstrappedClass;
        this.methods = new HashMap<>();
    }

    public void registerMethodTransformation(Method method, MethodTransformingPlan transformingPlan) {
        if (this.methods.containsKey(method)) {
            LOGGER.warning("Registering more than one transformer to one method, this can lead to unexpected behavior!");
        }

        this.methods.compute(method, (k, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.add(transformingPlan);

            return v;
        });
    }

    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

    public Map<Method, List<MethodTransformingPlan>> getMethods() {
        return Streams.to(this.methods.entrySet())
                .mapToPair(Map.Entry::getKey, Map.Entry::getValue)
                .mapSecond(Collections::unmodifiableList)
                .toUnmodifiableMap();
    }

    @Override
    public void transform(Class<?> element, BootstrappedClass bootstrapped) {

    }
}
