package tv.isshoni.winry.api.async;

import tv.isshoni.araragi.concurrent.async.IAsyncManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface IWinryAsyncManager extends IAsyncManager {

    boolean isRunning();

    <T> Future<T> submitToMain(Callable<T> callable);

    Future<?> submitToMain(Runnable runnable);

    <T> T forkMain(Callable<T> cont) throws ExecutionException, InterruptedException;

    Runnable nextMainCall();

    void shutdown();
}
