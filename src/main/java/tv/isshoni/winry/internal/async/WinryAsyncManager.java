package tv.isshoni.winry.internal.async;

import tv.isshoni.araragi.async.AsyncManager;
import tv.isshoni.winry.api.async.IWinryAsyncManager;

import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WinryAsyncManager extends AsyncManager implements IWinryAsyncManager {

    private final String contextName;

    private Thread newMain;

    private final Stack<Runnable> calls;

    public WinryAsyncManager(String contextName) {
        super();

        this.contextName = contextName;
        this.calls = new Stack<>();
    }

    @Override
    public boolean isRunning() {
        synchronized (this.calls) {
            return this.newMain != null && !this.calls.isEmpty();
        }
    }

    @Override
    public <T> Future<T> submitToMain(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        synchronized (this.calls) {
            this.calls.push(() -> {
                try {
                    future.complete(callable.call());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
            this.calls.notify();
        }

        return future;
    }

    @Override
    public Future<?> submitToMain(Runnable runnable) {
        CompletableFuture<String> future = new CompletableFuture<>();
        synchronized (this.calls) {
            this.calls.push(() -> {
                runnable.run();
                future.complete("");
            });
            this.calls.notify();
        }

        return future;
    }

    @Override
    public <T> T forkMain(Callable<T> cont) throws ExecutionException, InterruptedException {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.newMain = new Thread(() -> {
            try {
                future.complete(cont.call());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }

            synchronized (this.calls) {
                this.calls.notify();
            }
        }, "WinryManagerThread-" + this.contextName);
        this.newMain.start();

        while (!future.isDone()) {
            if (!this.calls.isEmpty()) {
                this.calls.pop().run();
            } else {
                synchronized (this.calls) {
                    try {
                        this.calls.wait();
                    } catch (InterruptedException ignored) { }
                }
            }
        }

        return future.get();
    }
}
