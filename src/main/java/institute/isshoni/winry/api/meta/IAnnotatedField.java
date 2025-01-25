package institute.isshoni.winry.api.meta;

import institute.isshoni.winry.internal.model.meta.IDeclared;
import institute.isshoni.winry.internal.model.meta.ITransformable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface IAnnotatedField extends IDeclared<Field>, ITransformable<Field> {

    Class<?> getType();

    Type getGenericType();
}
