package tv.isshoni.winry.reflection;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public enum ReflectedModifier {
    PUBLIC(Modifier::isPublic),
    PRIVATE(Modifier::isPrivate),
    PROTECTED(Modifier::isProtected),
    STATIC(Modifier::isStatic),
    FINAL(Modifier::isFinal),
    SYNCHRONIZED(Modifier::isSynchronized),
    VOLATILE(Modifier::isVolatile),
    TRANSIENT(Modifier::isTransient),
    NATIVE(Modifier::isNative),
    INTERFACE(Modifier::isInterface),
    ABSTRACT(Modifier::isAbstract),
    STRICT(Modifier::isStrict);

    private final Function<Integer, Boolean> supplier;

    ReflectedModifier(Function<Integer, Boolean> supplier) {
        this.supplier = supplier;
    }

    public static String toString(Member member) {
        return Modifier.toString(member.getModifiers());
    }

    public static Set<ReflectedModifier> getModifiers(Class<?> clazz) {
        return getModifiers(clazz.getModifiers());
    }

    public static Set<ReflectedModifier> getModifiers(Member member) {
        return getModifiers(member.getModifiers());
    }

    public static Set<ReflectedModifier> getModifiers(int modifiers) {
        Set<ReflectedModifier> result = new HashSet<>();

        for (ReflectedModifier modifier : values()) {
            if (modifier.supplier.apply(modifiers)) {
                result.add(modifier);
            }
        }

        return result;
    }
}
