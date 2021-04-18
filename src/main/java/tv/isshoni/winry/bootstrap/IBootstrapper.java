package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.bootstrap.element.IBootstrappedElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBootstrapper {

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    List<IBootstrappedElement<?, ?>> finalizeClasses(Bootstrap bootstrap, Map<Class<?>, BootstrappedClass<?>> clazzes, Map<Class<?>, Object> provided);

    Set<Class<?>> discoverClasses(Bootstrap bootstrap, Class<?> baseClazz);

    Map<Class<?>, BootstrappedClass<?>> prepareClasses(Bootstrap bootstrap, Set<Class<?>> clazzes);
}
