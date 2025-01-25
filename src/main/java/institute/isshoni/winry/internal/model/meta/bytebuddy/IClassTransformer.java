package institute.isshoni.winry.internal.model.meta.bytebuddy;

import institute.isshoni.winry.api.meta.IAnnotatedClass;

@FunctionalInterface
public interface IClassTransformer extends ITransformer<Class<?>, IAnnotatedClass> { }
