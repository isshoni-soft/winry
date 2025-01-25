package institute.isshoni.winry.api.meta;

import institute.isshoni.winry.internal.model.meta.IDeclared;
import institute.isshoni.winry.internal.model.meta.ITransformable;

import java.lang.reflect.Method;

public interface IAnnotatedMethod extends IDeclared<Method>, ITransformable<Method> {

    Class<?> getReturnType();
}
