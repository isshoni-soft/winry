package institute.isshoni.winry.internal.annotation.processor.type;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.winry.api.annotation.Config;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.service.ConfigService;

public class ConfigClassProcessor implements IWinryAnnotationProcessor<Config> {

    private final Constant<IWinryContext> context;

    private final ConfigService configService;

    public ConfigClassProcessor(@Context IWinryContext context, @Inject ConfigService configService) {
        this.context = new Constant<>(context);
        this.configService = configService;
    }

    @Override
    public void executeClass(IAnnotatedClass clazz, Object target, Config annotation) {
        this.configService.loadSingletonConfig(clazz.getElement(), target);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
