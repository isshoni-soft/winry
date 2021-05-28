package tv.isshoni.winry.annotation.manage;

import tv.isshoni.winry.annotation.Weight;
import tv.isshoni.winry.entity.annotation.IAnnotationWeightEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class WeightCalculator {

    public int calculateWeight(Annotation annotation) {
        Optional<Weight> weightOptional = Optional.of(annotation.annotationType().getAnnotation(Weight.class));

        Weight weight = weightOptional.orElseThrow(RuntimeException::new); // TODO: Make a dedicated exception to this

        // step into if weight can be dynamic
        int defaultWeight = getDefaultWeight(annotation, weight);

        if (!weight.dynamic().equals(Weight.NOT_DYNAMIC)) {
            Method dynamicWeight;
            try {
                dynamicWeight = annotation.annotationType().getMethod(weight.dynamic());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e); // TODO: Make a dedicated exception to this
            }

            if (!dynamicWeight.getReturnType().equals(Integer.class) && !dynamicWeight.getReturnType().equals(int.class)) {
                throw new RuntimeException(); // TODO: Make a dedicated exception to this
            }

            if (defaultWeight == weight.value()) {
                // weight is the default weight, return the value of the dynamic weight method.
                try {
                    return (Integer) dynamicWeight.invoke(annotation);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e); // TODO: Make a dedicated exception to this
                }
            } else { // the custom weight enum has a non-default value
                return defaultWeight;
            }
        } else {
            return defaultWeight;
        }
    }

    private int getDefaultWeight(Annotation annotation, Weight weight) {
        try {
            Method valueMethod = annotation.annotationType().getMethod(weight.weightEnum());

            if (valueMethod.getReturnType().isEnum() && IAnnotationWeightEnum.class.isAssignableFrom(valueMethod.getReturnType())) {
                IAnnotationWeightEnum type = (IAnnotationWeightEnum) valueMethod.invoke(annotation);

                return type.getWeight();
            } else {
                return weight.value(); // value might be something else, just return value stored in weight annotation
            }
        } catch (NoSuchMethodException e) {
            return weight.value();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e); // TODO: Make a dedicated exception to this
        }
    }
}
