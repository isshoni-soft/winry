package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;
import tv.isshoni.winry.entity.bootstrap.element.BootstrappedMethod;
import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;

import java.util.Map;
import java.util.stream.Stream;

public interface IBootstrapper {

    IAnnotationManager getAnnotationManager();

    IElementBootstrapper getElementBootstrapper();

    Map<Class<?>, Object> getProvided();

    void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided);

    void bootstrapClasses(Class<?> baseClass, Class<?>[] manual, String[] packages, Map<Class<?>, Object> provided);

    Stream<IBootstrappedElement> compileRunStream();

    <T> T execute(BootstrappedMethod bootstrapped);

    void inject(BootstrappedField bootstrapped, Object injected);

    void inject(BootstrappedField bootstrapped);
}
