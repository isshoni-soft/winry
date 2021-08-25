package tv.isshoni.winry.internal.annotation.processor.parameter;

import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.winry.annotation.parameter.Context;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.internal.context.WinryContext;

import java.util.Objects;

public class WinryContextProcessor implements IParameterSupplier<Context, IWinryContext> {

    @Override
    public IWinryContext supply(Context context, IWinryContext o) {
        if (Objects.isNull(o)) {
            return WinryContext.getContextFor(this).get();
        } else {
            return o;
        }
    }
}
