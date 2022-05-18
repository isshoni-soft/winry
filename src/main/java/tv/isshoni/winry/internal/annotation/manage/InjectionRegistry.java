package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.winry.entity.annotation.inject.IInjectionRegistry;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;

import java.util.HashMap;
import java.util.Map;

public class InjectionRegistry implements IInjectionRegistry {

    private final Map<String, Object> injected = new HashMap<>();

    private final IElementBootstrapper elementBootstrapper;

    public InjectionRegistry(IElementBootstrapper elementBootstrapper) {
        this.elementBootstrapper = elementBootstrapper;
    }

    @Override
    public Object getSingletonInjection(Class<?> key) {
        return this.elementBootstrapper.getBootstrappedClass(key).getObject();
    }

    @Override
    public Object getInjectedKey(String key) {
        return this.injected.get(key);
    }

    @Override
    public void registerInjection(String key, Object injection) {
        this.injected.put(key, injection);
    }
}