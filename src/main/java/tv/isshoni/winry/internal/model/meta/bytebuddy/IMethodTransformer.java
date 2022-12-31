package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.reflect.Method;

@FunctionalInterface
public interface IMethodTransformer extends ITransformer<Method, IAnnotatedMeta<Method>> { }
