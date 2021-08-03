package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedClass;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

import java.util.Map;
import java.util.stream.Stream;

public interface IBootstrapper {

    IWinryAnnotationManager getAnnotationManager();

    IElementBootstrapper getElementBootstrapper();

    ILoggerFactory getLoggerFactory();

    IWinryContext getContext();

    IAsyncManager getAsyncManager();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided);

    Stream<IBootstrappedElement> compileRunStream();

    // TODO: This is pretty atrocious, move these methods to ElementBootstrapper what was I thinking putting them here

    <T> T construct(BootstrappedClass bootstrapped);

    <T> T execute(BootstrappedMethod bootstrapped);

    void inject(BootstrappedField bootstrapped, Object injected);

    void inject(BootstrappedField bootstrapped);
}
