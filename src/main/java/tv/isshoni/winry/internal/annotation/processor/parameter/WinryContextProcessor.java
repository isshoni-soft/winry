package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IAnnotatedField;

import java.lang.reflect.Parameter;

@AttachTo(Context.class)
public class WinryContextProcessor implements IWinryAdvancedAnnotationProcessor<Context, IWinryContext> {

    private final AraragiLogger logger;

    private final Constant<IWinryContext> context;

    public WinryContextProcessor(IWinryContext context) {
        this.context = new Constant<>(context);
        this.logger = this.context.get().createLogger("WinryContextAnnotation");
    }

    @Override
    public void executeField(IAnnotatedField meta, Object target, Context annotation) {
        this.logger.debug("Context into field: " + meta.getDisplay());

        if (Constant.class.equals(meta.getType()) &&
                IWinryContext.class.isAssignableFrom(ReflectionUtil.getParameterizedTypes(meta.getGenericType())[0])) {
            this.context.get().getMetaManager().inject(meta, target, this.context);
        } else if (IWinryContext.class.isAssignableFrom(meta.getType())) {
            this.context.get().getMetaManager().inject(meta, target, this.context.get());
        } else {
            throw new IllegalStateException("Cannot inject IWinryContext into: " + meta.getType());
        }
    }

    @Override
    public IWinryContext supply(Context context, IWinryContext o, Parameter parameter) {
        return this.context.get();
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
