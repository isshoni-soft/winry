package tv.isshoni.winry.internal.model.meta.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.reflect.AnnotatedElement;

@FunctionalInterface
public interface ITransformer<E extends AnnotatedElement, M extends IAnnotatedMeta<E>> {

    DynamicType.Builder<?> transform(E element, M bootstrapped, DynamicType.Builder<?> builder);
}
