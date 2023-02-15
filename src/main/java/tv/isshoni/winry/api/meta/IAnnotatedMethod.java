package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.internal.model.meta.IDeclared;
import tv.isshoni.winry.internal.model.meta.ITransformable;

import java.lang.reflect.Method;

public interface IAnnotatedMethod extends IDeclared<Method>, ITransformable<Method> {

    Class<?> getReturnType();
}
