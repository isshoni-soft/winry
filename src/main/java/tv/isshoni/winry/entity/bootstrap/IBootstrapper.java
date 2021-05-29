package tv.isshoni.winry.entity.bootstrap;

import com.google.common.collect.ImmutableMap;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.manage.AnnotationManager;
import tv.isshoni.winry.bootstrap.ElementBootstrapper;
import tv.isshoni.winry.entity.element.BootstrappedField;
import tv.isshoni.winry.entity.element.BootstrappedMethod;
import tv.isshoni.winry.entity.element.IBootstrappedElement;

import java.util.List;
import java.util.Map;

public interface IBootstrapper {

    AnnotationManager getAnnotationManager();

    ElementBootstrapper getElementBootstrapper();

    ImmutableMap<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided);

    List<IBootstrappedElement<?>> finalizeClasses();

    <T> T execute(BootstrappedMethod bootstrapped);

    void inject(BootstrappedField bootstrapped, Object injected);

    void inject(BootstrappedField bootstrapped);
}
