package tv.isshoni.winry.internal.model.bootstrap;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.bootstrap.executable.IExecutable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface IBootstrapper {

    IWinryContext getContext();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void backload();

    void reprocess(Class<? extends Annotation>... annotations);

    List<IExecutable> compileRunList();

    Class<?> getBootstrapped();
}
