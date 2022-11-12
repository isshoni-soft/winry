package tv.isshoni.winry.internal.entity.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.winry.internal.entity.bootstrap.element.IBootstrappedElement;

import java.lang.reflect.AnnotatedElement;

public interface ITransformingPlan<E extends AnnotatedElement, B extends IBootstrappedElement<E>> {

    DynamicType.Builder<?> transform(E element, B bootstrapped, DynamicType.Builder<?> builder);
}
