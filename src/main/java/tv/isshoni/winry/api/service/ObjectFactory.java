package tv.isshoni.winry.api.service;

import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;

@Injected
public class ObjectFactory {

    @Inject
    private IWinryContext context;

    public <T> T construct(Class<T> clazz, Object... parameters) {
        IAnnotatedClass annotatedClass = this.context.getMetaManager().findMeta(clazz);

        if (annotatedClass == null) {
            annotatedClass = this.context.getMetaManager().generateMeta(clazz);
        }

        if (!annotatedClass.isTransformed()) {
            annotatedClass.transform(new WinryWrapperGenerator(this.context, annotatedClass));
        }

        T result;
        try {
            result = (T) annotatedClass.newInstance(parameters);
        } catch (Throwable e) {
            throw Exceptions.rethrow(e);
        }

        annotatedClass.regenerate(result);
        annotatedClass.execute(result);
        annotatedClass.getMethods().forEach(meta -> meta.execute(result));
        annotatedClass.getFields().forEach(meta -> meta.execute(result));

        return result;
    }
}
