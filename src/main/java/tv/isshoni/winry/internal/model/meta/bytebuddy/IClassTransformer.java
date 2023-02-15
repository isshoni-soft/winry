package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.api.meta.IAnnotatedClass;

@FunctionalInterface
public interface IClassTransformer extends ITransformer<Class<?>, IAnnotatedClass> { }
