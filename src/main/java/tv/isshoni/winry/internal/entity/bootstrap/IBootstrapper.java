package tv.isshoni.winry.internal.entity.bootstrap;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.bootstrap.IExecutable;

import java.util.List;
import java.util.Map;

public interface IBootstrapper {

    IWinryContext getContext();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided);

    List<IExecutable> compileRunList();
}
