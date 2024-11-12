package tv.isshoni.winry.internal.annotation.processor.type;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.Config;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.service.ConfigService;

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
