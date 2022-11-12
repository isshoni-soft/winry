package tv.isshoni.winry.api.async;

import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.context.IWinryContext;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Injected
public class AsyncService {

    @Inject
    private IWinryContext context;

    public Future<?> onMain(Runnable runnable) {
        return this.context.getAsyncManager().submitToMain(runnable);
    }

    public <T> Future<T> onMain(Callable<T> callable) {
        return this.context.getAsyncManager().submitToMain(callable);
    }

    public Future<?> async(Runnable runnable) {
        return this.context.getAsyncManager().submit(runnable);
    }

    public <T> Future<T> async(Callable<T> callable) {
        return this.context.getAsyncManager().submit(callable);
    }
}
