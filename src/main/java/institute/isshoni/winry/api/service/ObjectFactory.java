package institute.isshoni.winry.api.service;

import institute.isshoni.winry.api.annotation.Injected;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.context.IWinryContext;

@Injected
public class ObjectFactory {

    @Context private IWinryContext context;

    public <T> T construct(Class<T> clazz, Object... parameters) {
        return context.getAnnotationManager().winryConstruct(this.context, clazz, parameters);
    }
}
