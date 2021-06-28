package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public interface ITransformingBlueprint {

    void transform();

    void registerSimpleMethodDelegator(Method method, int weight, MethodDelegator delegator);

    void registerAdvancedClassTransformation(ClassTransformingPlan transformingPlan);

    void registerAdvancedMethodTransformation(Method method, MethodTransformingPlan transformingPlan);

    void registerAdvancedFieldTransformation(Field field, FieldTransformingPlan transformingPlan);

    BootstrappedClass getBootstrappedClass();

    Map<Method, ITransformingPlan<Method, BootstrappedMethod>> getMethodTransformers();

}
