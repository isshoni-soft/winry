package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.entity.context.IWinryContext;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

public class InjectParameterProcessor implements IParameterSupplier<Inject, Object> {

    private final IWinryContext context;

    public InjectParameterProcessor(@Context IWinryContext context) {
        this.context = context;
    }

    @Override
    public Object supply(Inject annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        Object injected;
        if (annotation.value().equals(Inject.DEFAULT)) { // No perceivable change in initial functionality
            injected = this.context.getInjectionRegistry().getSingletonInjection(parameter.getType());
        } else {
            injected = this.context.getInjectionRegistry().getInjectedKey(annotation.value());

            if (Objects.isNull(injected)) {
                injected = this.context.getElementBootstrapper().getBootstrappedClass(parameter.getType()).newInstance();

                this.context.registerToContext(injected);

                this.context.getInjectionRegistry().registerInjection(annotation.value(), injected);
            }
        }

        return injected;
    }
}
