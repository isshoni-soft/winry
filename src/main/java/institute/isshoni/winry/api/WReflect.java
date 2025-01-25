package institute.isshoni.winry.api;

import institute.isshoni.winry.api.meta.IWinryTransformed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class WReflect {

    public static Class<?> getClass(Class<?> clazz) {
        if (IWinryTransformed.class.isAssignableFrom(clazz)) {
            return clazz.getSuperclass();
        } else {
            return clazz;
        }
    }

    public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException {
        return getClass(clazz).getDeclaredMethod(name);
    }

    public static Method[] getMethods(Class<?> clazz) {
        return getClass(clazz).getMethods();
    }

    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        return getClass(clazz).getDeclaredField(name);
    }

    public static Field[] getFields(Class<?> clazz) {
        return getClass(clazz).getDeclaredFields();
    }
}
