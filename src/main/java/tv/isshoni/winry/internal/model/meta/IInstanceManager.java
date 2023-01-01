package tv.isshoni.winry.internal.model.meta;

public interface IInstanceManager {

    Object getSingletonInjection(Class<?> key);

    Object getKeyedInstance(String key, Class<?> type);
}
