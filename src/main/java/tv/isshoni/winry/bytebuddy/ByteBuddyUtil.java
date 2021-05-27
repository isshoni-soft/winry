package tv.isshoni.winry.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.bootstrap.element.BootstrappedClass;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

public class ByteBuddyUtil {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    public static DynamicType.Builder<?> wrapClass(BootstrappedClass<?> bootstrappedClass) {
        DynamicType.Builder<?> result = BYTE_BUDDY.subclass(bootstrappedClass.getBootstrappedElement())
                .defineMethod("isWinryWrapped", Boolean.TYPE, Modifier.PUBLIC | Modifier.STATIC)
                .intercept(FixedValue.value(true));

        AtomicReference<DynamicType.Builder<?>> builderReference = new AtomicReference<>(result);

        Arrays.stream(bootstrappedClass.getBootstrappedElement().getMethods())
                .filter(m -> m.isAnnotationPresent(Async.class))
                .forEach(m -> {
                    DynamicType.Builder<?> builder = builderReference.get();

                    builderReference.set(builder.method(named(m.getName())
                            .and(isDeclaredBy(m.getDeclaringClass()))
                            .and(returns(m.getReturnType())))
                            .intercept(to(AsyncManager.class)));
                });


        return builderReference.get();
    }
}
