package tv.isshoni.winry.internal.model.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface IAnnotatedClass extends IAnnotatedMeta<Class<?>> {

    <R> R newInstance() throws Throwable;

    List<IAnnotatedMeta<Method>> getMethods();

    List<IAnnotatedMeta<Field>> getFields();

    IAnnotatedMeta<Method> getMethod(Method method);

    IAnnotatedMeta<Method> getMethod(String name);
}
