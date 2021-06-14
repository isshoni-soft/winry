package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodTransformingPlan extends ITransformingPlan<Method, BootstrappedMethod> { }
