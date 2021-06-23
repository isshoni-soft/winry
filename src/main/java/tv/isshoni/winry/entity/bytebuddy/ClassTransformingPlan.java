package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;

@FunctionalInterface
public interface ClassTransformingPlan extends ITransformingPlan<Class<?>, BootstrappedClass> { }
