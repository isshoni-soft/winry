package tv.isshoni.winry.internal.model.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface IAnnotatedClass extends IAnnotatedMeta<Class<?>>, ITransformable<Class<?>> {

    <R> R newInstance() throws Throwable;

    List<IAnnotatedMethod> getMethods();

    List<IAnnotatedField> getFields();

    IAnnotatedMethod getMethod(Method method);

    IAnnotatedMethod getMethod(String name);

    IAnnotatedField getField(Field field);

    IAnnotatedField getField(String name);
}
