package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.ITransformable;
import tv.isshoni.winry.internal.model.meta.ITransformedClass;

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
