package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.bootstrap.BootstrappedClass;

import java.util.Set;

public interface IBootstrapper {

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Object[] provided);
    void finalizeClasses(Bootstrap bootstrap, Set<BootstrappedClass<?>> clazzes);

    Set<Class<?>> discoverClasses(Bootstrap bootstrap, Class<?> baseClazz);

    Set<BootstrappedClass<?>> prepareClasses(Bootstrap bootstrap, Set<Class<?>> clazzes);
}
