package tv.isshoni.winry.api.context;

import java.util.Optional;

@Deprecated
public interface IContextual {

    default Optional<IWinryContext> getOptionalWinryContext() {
        return WinryContext.getContextFor(this);
    }

    default IWinryContext getWinryContext() {
        return this.getOptionalWinryContext().get();
    }
}
