package tv.isshoni.winry.internal;

import tv.isshoni.araragi.functional.ObjHelpers;
import tv.isshoni.araragi.logging.model.ILoggerFactory;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IBootstrapContext;

public class BootstrapContext implements IBootstrapContext {

    private final String[] arguments;

    private final IWinryAsyncManager asyncManager;
    private final ILoggerFactory loggerFactory;

    private final boolean forked;

    private BootstrapContext(Builder builder) {
        this.arguments = builder.arguments;
        this.asyncManager = builder.asyncManager;
        this.loggerFactory = builder.loggerFactory;
        this.forked = builder.forked;
    }

    @Override
    public String[] getArguments() {
        return this.arguments;
    }

    @Override
    public IWinryAsyncManager getAsyncManager() {
        return this.asyncManager;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    @Override
    public boolean isForked() {
        return this.forked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String[] arguments;
        private IWinryAsyncManager asyncManager;
        private ILoggerFactory loggerFactory;
        private boolean forked;

        public Builder arguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder forked(boolean forked) {
            this.forked = forked;
            return this;
        }

        public Builder asyncManager(IWinryAsyncManager asyncManager) {
            this.asyncManager = asyncManager;
            return this;
        }

        public Builder loggerFactory(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
            return this;
        }

        public BootstrapContext build() {
            if (ObjHelpers.isOneNull(this.asyncManager, this.forked, this.loggerFactory, this.arguments)) {
                throw new IllegalStateException("Cannot build without all properties present!");
            }

            return new BootstrapContext(this);
        }
    }
}
