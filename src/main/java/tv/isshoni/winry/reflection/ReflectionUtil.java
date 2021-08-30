package tv.isshoni.winry.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.reflections8.Reflections;
import org.reflections8.scanners.ResourcesScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;

public class ReflectionUtil {

    public static Reflections classFinder(String[] packages, Class<?>... references) {
        FilterBuilder filter = new FilterBuilder().includePackage(packages);

        for (Class<?> clazz : references) {
            filter.includePackage(clazz);
        }

        return new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false), new ResourcesScanner())
                .forPackages(packages)
                .filterInputsBy(filter));
    }

    public static <T> T execute(Class<?> from, Object target, String methodName, Object... parameters) {
        try {
            return (T) from.getMethod(methodName, convertObjectsToClass(parameters)).invoke(target, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Class<T> clazz, Object... parameters) {
        try {
            return construct(clazz.getConstructor(convertObjectsToClass(parameters)), parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Constructor<T> constructor, Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectField(Field field, Object target, Object injected) {
        boolean couldAccess = field.canAccess(target);

        if (!couldAccess) {
            field.setAccessible(true);
        }

        try {
            field.set(target, injected);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!couldAccess) {
            field.setAccessible(false);
        }
    }

    public static Class<?>[] convertObjectsToClass(Object... objects) {
        Class<?>[] result = new Class<?>[objects.length];

        for (int x = 0; x < objects.length; x++) {
            result[x] = objects[x].getClass();
        }

        return result;
    }
}
