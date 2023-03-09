package tv.isshoni.winry.api.service;

import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.context.IWinryContext;

@Injected
public class ObjectFactory {

    @Inject
    private IWinryContext context;

    public <T> T construct(Class<T> clazz, Object... parameters) {
        return context.getAnnotationManager().winryConstruct(this.context, clazz, parameters);
    }
}
