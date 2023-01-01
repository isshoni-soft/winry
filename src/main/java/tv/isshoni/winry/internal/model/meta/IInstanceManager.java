package tv.isshoni.winry.internal.model.meta;

public interface IInstanceManager {

    void registerSingletonInstance(IAnnotatedClass classMeta, Object object);

    void registerKeyedInstance(IAnnotatedClass classMeta, String key, Object object);

    Object getSingletonInjection(Class<?> key);

    Object getKeyedInstance(String key, Class<?> type);
}
