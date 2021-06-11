package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface IElementBootstrapper {

    void bootstrap(Class<?> clazz);

    void bootstrap(Method method);

    void bootstrap(Field field);

    BootstrappedClass getDeclaringClass(Member member);
}
