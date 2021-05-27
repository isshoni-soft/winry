package tv.isshoni.winry.entity.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public interface AnnotationProcessor {

    default void onClass(Class<?> clazz) { }

    default void onField(Field field) { }

    default void onMethod(Method method) { }

    default List<Class<? extends Annotation>> getIncompatibleWith() {
        return new LinkedList<>();
    }
}
