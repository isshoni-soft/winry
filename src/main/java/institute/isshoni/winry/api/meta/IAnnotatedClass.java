package institute.isshoni.winry.api.meta;

import institute.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import institute.isshoni.winry.internal.model.meta.ITransformable;
import institute.isshoni.winry.internal.model.meta.ITransformedClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface IAnnotatedClass extends IAnnotatedMeta<Class<?>>, ITransformable<Class<?>>, ITransformedClass {

    void regenerate(Object target);

    Object newInstance(Object... parameters) throws Throwable;

    List<IAnnotatedMethod> getMethods();

    List<IAnnotatedField> getFields();

    IAnnotatedMethod getMethod(Method method);

    IAnnotatedMethod getMethod(String name);

    IAnnotatedField getField(Field field);

    IAnnotatedField getField(String name);
}
