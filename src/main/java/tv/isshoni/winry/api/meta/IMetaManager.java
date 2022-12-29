package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

public interface IMetaManager {

    IAnnotatedMeta<Class<?>> generateMeta(Class<?> element);

    <E extends AnnotatedElement> IAnnotatedMeta<E> getMetaFor(E element);

    <E extends AnnotatedElement, R extends IAnnotatedMeta<E>> R getMeta(E element, Class<R> type);

    Object construct(IAnnotatedMeta<Class<?>> meta, boolean dirty);

    default <T> T construct(Class<T> type, boolean dirty) {
        IAnnotatedMeta<Class<?>> meta = getMetaFor(type);

        if (Objects.isNull(meta)) {
            meta = generateMeta(type);
        }

        return (T) construct(meta, dirty);
    }
}
