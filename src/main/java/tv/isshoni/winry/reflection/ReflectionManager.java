package tv.isshoni.winry.reflection;

import com.google.common.base.Preconditions;
import tv.isshoni.winry.entity.element.BootstrappedClass;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.entity.element.BootstrappedMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionManager {

    private static final Map<Class<?>, BootstrappedClass<?>> CLASS_REGISTRY = new HashMap<>();

    public static void registerClass(BootstrappedClass<?> bootstrapped) {
        Class<?> clazz = bootstrapped.getBootstrappedElement();

        if (CLASS_REGISTRY.containsKey(clazz)) {
            throw new IllegalStateException(clazz.getName() + " is already registered to the class registry!");
        }

        CLASS_REGISTRY.put(clazz, bootstrapped);
    }

    public static <T> T executeMethod(Class<?> from, Object target, String methodName, Object... parameters) {
        try {
            return (T) from.getMethod(methodName, convertObjectsToClass(parameters)).invoke(target, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T executeMethod(BootstrappedMethod bootstrapped) {
        Method method = bootstrapped.getBootstrappedElement();
        Object target = null;

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = findDeclaringClassInRegistry(method).getObject();
        }

        try {
            return (T) method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Class<T> clazz, Object... parameters) {
        try {
            return clazz.getConstructor(convertObjectsToClass(parameters)).newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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

    public static void injectField(BootstrappedField<?> bootstrapped, Object injected) {
        Object target = null;
        Field field = bootstrapped.getBootstrappedElement();

        if (!bootstrapped.getModifiers().contains(ReflectedModifier.STATIC)) {
            target = findDeclaringClassInRegistry(field).getObject();
        }

        injectField(field, target, injected);
    }

    public static void injectField(BootstrappedField<?> bootstrapped) {
        Preconditions.checkNotNull(bootstrapped);
        Preconditions.checkNotNull(bootstrapped.getTarget());
        Preconditions.checkNotNull(bootstrapped.getTarget().getObject());

        injectField(bootstrapped, bootstrapped.getTarget().getObject());
    }

    public static BootstrappedClass<?> findDeclaringClassInRegistry(Member member) {
        BootstrappedClass<?> bootstrappedClass = CLASS_REGISTRY.get(member.getDeclaringClass());

        if (bootstrappedClass == null) {
            throw new IllegalStateException("Unable to find " + member.getDeclaringClass() + " in class registry!");
        }

        if (!bootstrappedClass.hasObject()) {
            throw new IllegalStateException("Tried injecting field into non-booted class: " + bootstrappedClass);
        }

        return bootstrappedClass;
    }

    public static Class<?>[] convertObjectsToClass(Object... objects) {
        Class<?>[] result = new Class<?>[objects.length];

        for (int x = 0; x < objects.length; x++) {
            result[x] = objects[x].getClass();
        }

        return result;
    }
}
