package institute.isshoni.winry.api.meta;

import institute.isshoni.winry.api.context.IWinryContext;

import java.util.Map;
import java.util.Set;

public interface IMetaManager {

    void setContext(IWinryContext context);

    IWinryContext getContext();

    ISingletonAnnotatedClass generateSingletonMeta(Class<?> element) throws Throwable;

    ISingletonAnnotatedClass generateSingletonMeta(Class<?> element, Object object) throws Throwable;

    IAnnotatedClass generateMeta(Class<?> element);

    ISingletonAnnotatedClass getSingletonMeta(Object element);

    IAnnotatedClass getMeta(Object element);

    IAnnotatedClass findMeta(Object element);

    Set<ISingletonAnnotatedClass> getAllSingletonClasses();

    void inject(IAnnotatedField field, Object instance, Object value);

    <R> R execute(IAnnotatedMethod method, Object instance, Map<String, Object> runtimeContext);
}
