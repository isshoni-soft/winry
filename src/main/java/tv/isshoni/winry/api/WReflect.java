package tv.isshoni.winry.api;

import tv.isshoni.winry.api.meta.IWinryTransformed;

import java.lang.reflect.Field;

public final class WReflect {

    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        if (IWinryTransformed.class.isAssignableFrom(clazz)) {
            return clazz.getSuperclass().getDeclaredField(name);
        } else {
            return clazz.getDeclaredField(name);
        }
    }

    public static Field[] getFields(Class<?> clazz) {
        if (IWinryTransformed.class.isAssignableFrom(clazz)) {
            return clazz.getSuperclass().getDeclaredFields();
        } else {
            return clazz.getDeclaredFields();
        }
    }
}
