package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;

import java.util.Objects;

public interface IMetaManager {

    void setContext(IWinryContext context);

    IWinryContext getContext();

    IAnnotatedClass generateMeta(Class<?> element);

    IAnnotatedClass getMeta(Object element);

    <R> R construct(IAnnotatedClass meta) throws Throwable;

    default <T> T construct(Class<T> type) {
        IAnnotatedClass meta = getMeta(type);

        if (Objects.isNull(meta)) {
            meta = generateMeta(type);
        }

        try {
            return construct(meta);
        } catch (Throwable e) {
            getContext().getExceptionManager().toss(e);
            return null;
        }
    }
}
