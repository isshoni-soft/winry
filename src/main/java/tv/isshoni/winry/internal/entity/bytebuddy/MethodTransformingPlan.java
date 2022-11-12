package tv.isshoni.winry.internal.entity.bytebuddy;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.bytebuddy.WinryMethodTransformer;

import java.lang.reflect.Method;
import java.util.Optional;

@FunctionalInterface
public interface MethodTransformingPlan extends ITransformingPlan<Method, BootstrappedMethod> {

    default boolean isWinry() {
        return WinryMethodTransformer.class.isAssignableFrom(this.getClass());
    }

    default Optional<WinryMethodTransformer> asWinry() {
        if (!isWinry()) {
            return Optional.empty();
        }

        return Optional.of((WinryMethodTransformer) this);
    }
}
