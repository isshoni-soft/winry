package institute.isshoni.winry.api.context;

import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.winry.api.async.IWinryAsyncManager;

public interface IBootstrapContext {

    String[] getArguments();

    IWinryAsyncManager getAsyncManager();

    ILoggerFactory getLoggerFactory();

    boolean isForked();

    Class<?> getBootstrappedClass();
}
