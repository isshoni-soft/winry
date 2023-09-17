package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.ITransformable;
import tv.isshoni.winry.internal.model.meta.ITransformedClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IAnnotatedClass extends IAnnotatedMeta<Class<?>>, ITransformable<Class<?>>, ITransformedClass {

    void regenerate(Object target);

    Object newInstance(Object... parameters) throws Throwable;

    Set<IAnnotatedClass> getDepends();

    List<IAnnotatedMethod> getMethods();

    List<IAnnotatedField> getFields();

    IAnnotatedMethod getMethod(Method method);

    IAnnotatedMethod getMethod(String name);

    IAnnotatedField getField(Field field);

    IAnnotatedField getField(String name);

    default int recursiveDepCount(Set<IAnnotatedClass> deps, Set<IAnnotatedClass> visited, int sum) {
        for (IAnnotatedClass dep : deps) {
            if (visited.contains(dep)) {
                continue;
            }
            visited.add(dep);

            sum += 1 + recursiveDepCount(dep.getDepends(), visited, sum);
        }

        return sum;
    }

    @Override
    default int getWeight() {
        int annoWeight = IAnnotatedMeta.super.getWeight();

        if (this.getDepends().isEmpty()) {
            return annoWeight;
        }

        return annoWeight - recursiveDepCount(this.getDepends(), new HashSet<>(), 0);
    }
}
