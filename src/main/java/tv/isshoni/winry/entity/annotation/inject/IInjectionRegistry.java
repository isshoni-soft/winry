package tv.isshoni.winry.entity.annotation.inject;

public interface IInjectionRegistry {

    Object getSingletonInjection(Class<?> key);

    Object getInjectedKey(String key);

    void registerInjection(String key, Object injection);
}
