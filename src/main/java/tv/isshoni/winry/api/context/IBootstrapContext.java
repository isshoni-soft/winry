package tv.isshoni.winry.api.context;

import tv.isshoni.winry.api.async.IWinryAsyncManager;

public interface IBootstrapContext {

    IWinryAsyncManager getAsyncManager();

    boolean isForked();
}
