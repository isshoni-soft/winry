package tv.isshoni.winry.internal.entity.bytebuddy;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public interface ITransformingBlueprint {

    void transform();

    void setClassTransformingPlan(ClassTransformingPlan transformingPlan);

    void setMethodTransformingPlan(Method method, MethodTransformingPlan transformingPlan);

    void setFieldTransformingPlan(Field field, FieldTransformingPlan transformingPlan);

    BootstrappedClass getBootstrappedClass();

    MethodTransformingPlan getMethodTransformingPlan(Method method);

    FieldTransformingPlan getFieldTransformingPlan(Field field);

    MethodTransformingPlan supplyDefaultMethodTransformingPlan(BootstrappedMethod method);

    FieldTransformingPlan supplyDefaultFieldTransformingPlan();

    Map<Method, ITransformingPlan<Method, BootstrappedMethod>> getMethodTransformers();

    Map<Field, ITransformingPlan<Field, BootstrappedField>> getFieldTransformers();

}
