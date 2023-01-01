package tv.isshoni.winry.internal.model.annotation.inject;

@Deprecated
public interface IInjectionRegistry {

    Object getSingletonInjection(Class<?> key);

    Object getInjectedKey(String key);

    void registerInjection(String key, Object injection);
}
