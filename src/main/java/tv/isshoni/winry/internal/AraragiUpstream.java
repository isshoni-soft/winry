package tv.isshoni.winry.internal;

import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// This class is basically a staging ground for methods that will be upstreamed into Araragi
@Deprecated
public class AraragiUpstream {

    public static Set<Class<?>> getAllTypesForConstruction(IWinryAnnotationManager annotationManager, Class<?> clazz) {
        HashSet<Class<?>> result = new HashSet<>();
        Constructor<?> constructor = annotationManager.discoverConstructor(clazz, false);

        if (constructor == null) {
            return result;
        }

        result.addAll(Arrays.asList(constructor.getParameterTypes()));

        return result;
    }

    public static Set<Class<? extends Annotation>> getAllAnnotationsForConstruction(IWinryAnnotationManager annotationManager, Class<?> clazz) {
        HashSet<Class<? extends Annotation>> result = new HashSet<>();
        Constructor<?> constructor = annotationManager.discoverConstructor(clazz, false);

        if (constructor == null) {
            return result;
        }

        result.addAll(ReflectionUtil.getAllParameterAnnotationTypes(constructor));
        Streams.to(constructor.getParameterTypes()).forEach(c ->
                result.addAll(getAllAnnotationsForConstruction(annotationManager, c)));

        return result;
    }
}
