package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;

import java.lang.reflect.AnnotatedElement;

public interface ITransformingPlan<E extends AnnotatedElement, B extends IBootstrappedElement<E>> {

    void transform(E element, B bootstrapped);
}
