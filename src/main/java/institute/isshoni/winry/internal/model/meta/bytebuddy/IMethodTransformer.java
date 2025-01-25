package institute.isshoni.winry.internal.model.meta.bytebuddy;

import institute.isshoni.winry.api.meta.IAnnotatedMethod;

import java.lang.reflect.Method;

@FunctionalInterface
public interface IMethodTransformer extends ITransformer<Method, IAnnotatedMethod> { }
