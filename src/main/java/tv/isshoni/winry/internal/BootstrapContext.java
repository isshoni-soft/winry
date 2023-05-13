package tv.isshoni.winry.internal;

import tv.isshoni.araragi.functional.ObjHelpers;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IBootstrapContext;

public class BootstrapContext implements IBootstrapContext {

    private final IWinryAsyncManager asyncManager;

    private final boolean forked;

    private BootstrapContext(Builder builder) {
        this.asyncManager = builder.asyncManager;
        this.forked = builder.forked;
    }

    @Override
    public IWinryAsyncManager getAsyncManager() {
        return this.asyncManager;
    }

    @Override
    public boolean isForked() {
        return this.forked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private IWinryAsyncManager asyncManager;
        private boolean forked;

        public Builder forked(boolean forked) {
            this.forked = forked;
            return this;
        }

        public Builder asyncManager(IWinryAsyncManager asyncManager) {
            this.asyncManager = asyncManager;
            return this;
        }

        public BootstrapContext build() {
            if (ObjHelpers.isOneNull(this.asyncManager, this.forked)) {
                throw new IllegalStateException("Cannot build without all properties present!");
            }

            return new BootstrapContext(this);
        }
    }
}
