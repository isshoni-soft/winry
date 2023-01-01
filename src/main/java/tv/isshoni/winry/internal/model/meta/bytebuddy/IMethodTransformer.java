package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

import java.lang.reflect.Method;

@FunctionalInterface
public interface IMethodTransformer extends ITransformer<Method, IAnnotatedMethod> { }
