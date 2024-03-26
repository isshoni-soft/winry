package tv.isshoni.winry.api.context;

import tv.isshoni.araragi.logging.model.ILoggerFactory;
import tv.isshoni.winry.api.async.IWinryAsyncManager;

public interface IBootstrapContext {

    String[] getArguments();

    IWinryAsyncManager getAsyncManager();

    ILoggerFactory getLoggerFactory();

    boolean isForked();
}
