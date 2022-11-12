package tv.isshoni.winry.internal.entity.bytebuddy;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;

@FunctionalInterface
public interface ClassTransformingPlan extends ITransformingPlan<Class<?>, BootstrappedClass> { }
