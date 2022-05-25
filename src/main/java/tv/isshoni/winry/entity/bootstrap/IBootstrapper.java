package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.util.List;
import java.util.Map;

public interface IBootstrapper {

    IWinryContext getContext();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided);

    List<IExecutable> compileRunList();

    // TODO: This is pretty atrocious, move these methods to ElementBootstrapper what was I thinking putting them here
}
