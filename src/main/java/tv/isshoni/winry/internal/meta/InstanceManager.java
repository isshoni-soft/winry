package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.data.collection.map.SubMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.context.IInstanceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InstanceManager implements IInstanceManager {

    private final AraragiLogger logger;

    private final IMetaManager metaManager;

    private final Map<IAnnotatedClass, Object> singletons;

    private final SubMap<String, IAnnotatedClass, Object, HashMap<IAnnotatedClass, Object>> keyedInstances;

    public InstanceManager(ILoggerFactory loggerFactory, IMetaManager metaManager) {
        this.metaManager = metaManager;
        this.logger = loggerFactory.createLogger("InstanceManager");
        this.singletons = new HashMap<>();
        this.keyedInstances = new SubMap<>(HashMap::new);
    }

    @Override
    public void registerSingletonInstance(IAnnotatedClass classMeta, Object object) {
        if (this.singletons.containsKey(classMeta)) {
            this.logger.warn("Cannot register duplicate singleton for: " + classMeta.getDisplay());
            return;
        }

        this.singletons.put(classMeta, object);
    }

    @Override
    public void registerKeyedInstance(IAnnotatedClass classMeta, String key, Object object) {
        if (this.keyedInstances.containsKey(key, classMeta)) {
            this.logger.warn("Cannot register duplicate singleton for: " + classMeta.getDisplay() + " -- key: " + key);
            return;
        }

        this.keyedInstances.put(key, Pair.of(classMeta, object));
    }

    @Override
    public <T> T getSingletonInjection(Class<T> key) {
        return (T) this.singletons.get(this.metaManager.getSingletonMeta(key));
    }

    @Override
    public <T> T getKeyedInstance(String key, Class<T> type) {
        return (T) Optional.ofNullable(this.keyedInstances.get(key))
                .map(m -> m.get(this.metaManager.getSingletonMeta(type)))
                .orElse(null);
    }

    @Override
    public <T> Optional<T> hasSingletonFor(Class<T> key) {
        return Optional.ofNullable(this.getSingletonInjection(key));
    }

    @Override
    public <T> Optional<T> hasKeyedInstanceFor(String key, Class<T> type) {
        return Optional.ofNullable(this.getKeyedInstance(key, type));
    }
}
