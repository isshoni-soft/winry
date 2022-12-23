package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.dynamic.DynamicType;
import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.model.bytebuddy.FieldTransformingPlan;

import java.lang.reflect.Field;

public class WinryFieldTransformer implements FieldTransformingPlan {

    @Override
    public DynamicType.Builder<?> transform(Field element, BootstrappedField bootstrapped, DynamicType.Builder<?> builder) {
        return builder.defineField(element.getName(), element.getType(), element.getModifiers())
                .annotateField(element.getAnnotations());
    }
}
