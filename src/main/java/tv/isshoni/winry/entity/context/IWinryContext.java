package tv.isshoni.winry.entity.context;

import tv.isshoni.araragi.async.IAsyncManager;
import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.entity.bootstrap.IBootstrapper;
import tv.isshoni.winry.entity.bootstrap.IElementBootstrapper;
import tv.isshoni.winry.entity.logging.ILoggerFactory;

public interface IWinryContext {

    void register(Object object);

    IBootstrapper getBootstrapper();

    IWinryAnnotationManager getAnnotationManager();

    ILoggerFactory getLoggerFactory();

    IElementBootstrapper getElementBootstrapper();

    IAsyncManager getAsyncManager();

    Bootstrap getBootstrapAnnotation();
}
