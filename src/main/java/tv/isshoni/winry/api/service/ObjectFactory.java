package tv.isshoni.winry.api.service;

import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

@Injected
public class ObjectFactory {

    @Inject
    private IWinryContext context;

    public <T> T construct(Class<T> clazz) {
        IAnnotatedClass annotatedClass = this.context.getMetaManager().generateMeta(clazz);
        annotatedClass.regenerate();

        if (!annotatedClass.isTransformed()) {
            annotatedClass.transform(new WinryWrapperGenerator(this.context, annotatedClass));
        }
        annotatedClass.execute();
        annotatedClass.getMethods().forEach(IAnnotatedMeta::execute);
        annotatedClass.getFields().forEach(IAnnotatedMeta::execute);

        return (T) annotatedClass.getInstance();
    }

    public <T> T construct(Class<T> clazz, Object... parameters) {
        // TODO: Feed parameters into constructor, also probably will require a change to underlying annotation processor
        // TODO: constructor processes
    }
}
