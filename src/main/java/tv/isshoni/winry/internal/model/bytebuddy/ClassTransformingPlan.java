package tv.isshoni.winry.internal.model.bytebuddy;

import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedClass;

@FunctionalInterface
public interface ClassTransformingPlan extends ITransformingPlan<Class<?>, BootstrappedClass> { }
