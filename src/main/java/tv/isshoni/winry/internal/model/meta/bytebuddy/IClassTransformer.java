package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;

@FunctionalInterface
public interface IClassTransformer extends ITransformer<Class<?>, IAnnotatedClass> { }
