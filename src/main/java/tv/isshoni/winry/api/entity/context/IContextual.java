package tv.isshoni.winry.api.entity.context;

import tv.isshoni.winry.internal.context.WinryContext;

import java.util.Optional;

public interface IContextual {

    default Optional<IWinryContext> getOptionalWinryContext() {
        return WinryContext.getContextFor(this);
    }

    default IWinryContext getWinryContext() {
        return this.getOptionalWinryContext().get();
    }
}
