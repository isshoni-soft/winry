package tv.isshoni.winry.internal.entity.bootstrap;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.internal.entity.bytebuddy.ITransformingBlueprint;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public interface IElementBootstrapper {

    ITransformingBlueprint supplyTransformingBlueprint(BootstrappedClass bootstrappedClass);

    BootstrappedClass getBootstrappedClass(Class<?> clazz);

    BootstrappedMethod getBootstrappedMethod(Method method);

    BootstrappedField getBootstrappedField(Field field);

    Collection<BootstrappedClass> getBootstrappedClasses();

    Collection<BootstrappedMethod> getBootstrappedMethod();

    Collection<BootstrappedField> getBootstrappedField();

    BootstrappedClass bootstrap(Class<?> clazz);

    void bootstrap(Method method);

    void bootstrap(Field field);

    BootstrappedClass getDeclaringClass(Member member);

    <T> T construct(BootstrappedClass bootstrapped);

    <T> T execute(BootstrappedMethod bootstrapped);

    <T> T execute(BootstrappedMethod bootstrapped, Map<String, Object> runtimeContext);

    void inject(BootstrappedField bootstrapped, Object injected);

    void inject(BootstrappedField bootstrapped);
}
