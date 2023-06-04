package tv.isshoni.winry.api.context;

import tv.isshoni.winry.api.meta.IAnnotatedClass;

import java.util.Optional;

public interface IInstanceManager {

    void registerSingletonInstance(IAnnotatedClass classMeta, Object object);

    void registerKeyedInstance(IAnnotatedClass classMeta, String key, Object object);

    <T> T getSingletonInjection(Class<? extends T> key);

    <T> T getKeyedInstance(String key, Class<? extends T> type);

    <T> Optional<T> hasSingletonFor(Class<? extends T> key);

    <T> Optional<T> hasKeyedInstanceFor(String key, Class<? extends T> type);
}
