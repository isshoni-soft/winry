package institute.isshoni.winry.api.async;

import institute.isshoni.winry.api.annotation.Injected;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.context.IWinryContext;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Injected
public class AsyncService {

    @Context private IWinryContext context;

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

    public boolean isRunning() {
        return this.context.getAsyncManager().isRunning();
    }
}
