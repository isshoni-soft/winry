package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.internal.context.WinryContext;

import java.util.Objects;

public class WinryContextProcessor implements IParameterSupplier<Context, IWinryContext> {

    @Override
    public IWinryContext supply(Context context, IWinryContext o) {
        Object otherContext = WinryContext.getContextFor(this).get();

        if (Objects.isNull(o)) {
            return (IWinryContext) otherContext;
        } else {
            return o;
        }
    }
}
