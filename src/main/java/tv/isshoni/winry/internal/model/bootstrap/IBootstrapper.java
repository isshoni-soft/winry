package tv.isshoni.winry.internal.model.bootstrap;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.bootstrap.executable.IExecutable;

import java.util.List;
import java.util.Map;

public interface IBootstrapper {

    IWinryContext getContext();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void backload();

    List<IExecutable> compileRunList();

    Class<?> getBootstrapped();
}
