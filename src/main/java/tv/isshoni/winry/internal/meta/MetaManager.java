package tv.isshoni.winry.internal.meta;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.api.meta.IMetaManager;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

public class MetaManager implements IMetaManager {

    private final Map<Class<?>, IAnnotatedMeta<?>> storedClassMetas;

    public MetaManager() {
        this.storedClassMetas = new HashMap<>();
    }

    @Override
    public IAnnotatedMeta<Class<?>> generateMeta(Class<?> element) {
        return null;
    }

    @Override
    public <E extends AnnotatedElement> IAnnotatedMeta<E> getMetaFor(E element) {
        return null;
    }

    @Override
    public <E extends AnnotatedElement, R extends IAnnotatedMeta<E>> R getMeta(E element, Class<R> type) {
        return null;
    }

    @Override
    public Object construct(IAnnotatedMeta<Class<?>> meta, boolean dirty) {
        return null;
    }
}
