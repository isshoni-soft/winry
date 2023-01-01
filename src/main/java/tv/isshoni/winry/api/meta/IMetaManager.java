package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedField;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMethod;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface IMetaManager {

    void setContext(IWinryContext context);

    IWinryContext getContext();

    IAnnotatedClass generateMeta(Class<?> element);

    IAnnotatedClass getMeta(Object element);

    Set<IAnnotatedClass> getAllClasses();

    <R> R construct(IAnnotatedClass meta) throws Throwable;

    void inject(IAnnotatedField field, Object instance, Object value);

    <R> R execute(IAnnotatedMethod method, Object instance, Map<String, Object> runtimeContext);

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
