package tv.isshoni.winry.internal.model.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

public interface IMetaManager {

    <E extends AnnotatedElement> IAnnotatedMeta<E> generateMeta(E element);

    <E extends AnnotatedElement> IAnnotatedMeta<E> getMetaFor(E element);

    Object construct(IAnnotatedMeta<Class<?>> meta, boolean dirty);

    default <T> T construct(Class<T> type, boolean dirty) {
        IAnnotatedMeta<Class<?>> meta = getMetaFor(type);

        if (Objects.isNull(meta)) {
            meta = generateMeta(type);
        }

        return (T) construct(meta, dirty);
    }
}
