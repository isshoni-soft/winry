package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.bootstrap.BootstrapClassType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBootstrapper {

    Set<Class<?>> discoverClasses(Bootstrap bootstrap, Class<?> baseClazz);

    Map<BootstrapClassType, List<Class<?>>> organizeClasses(Bootstrap bootstrap, Set<Class<?>> clazzes);

    void bootstrap(Bootstrap bootstrap, Map<BootstrapClassType, List<Class<?>>> organizedClazzes);
}
