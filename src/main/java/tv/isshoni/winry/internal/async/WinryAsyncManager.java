package tv.isshoni.winry.internal.async;

import tv.isshoni.araragi.concurrent.async.AsyncManager;
import tv.isshoni.araragi.exception.Exceptions;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.async.IWinryAsyncManager;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WinryAsyncManager extends AsyncManager implements IWinryAsyncManager {

    private final String contextName;

    private Thread newMain;

    private final Queue<Runnable> calls;

    private final AtomicBoolean running;

    private CompletableFuture<?> mainFuture;

    public WinryAsyncManager(String contextName) {
        super();

        this.contextName = contextName;
        this.running = new AtomicBoolean(false);
        this.calls = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean isRunning() {
        return this.newMain != null && !this.calls.isEmpty() || this.running.get();
    }

    @Override
    public <T> Future<T> submitToMain(Callable<T> callable) {
        if (isMainThread()) {
            try {
                return CompletableFuture.completedFuture(callable.call());
            } catch (Exception e) {
                throw Exceptions.rethrow(e);
            }
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        synchronized (this.calls) {
            this.calls.add(() -> {
                try {
                    future.complete(callable.call());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                    throw Exceptions.rethrow(e);
                }
            });
            this.calls.notify();
        }

        return future;
    }

    @Override
    public Future<?> submitToMain(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
            return CompletableFuture.completedFuture("");
        }

        CompletableFuture<String> future = new CompletableFuture<>();
        synchronized (this.calls) {
            this.calls.add(() -> {
                runnable.run();
                future.complete("");
            });
            this.calls.notify();
        }

        return future;
    }

    @Override
    public <T> T forkMain(Callable<T> cont) throws ExecutionException, InterruptedException {
        this.running.set(true);
        this.mainFuture = new CompletableFuture<T>();
        AtomicReference<Throwable> error = new AtomicReference<>(null);
        this.newMain = new Thread(() -> {
            try {
                ((CompletableFuture<T>) mainFuture).complete(cont.call());
            } catch (Throwable e) {
                error.set(e);
                mainFuture.completeExceptionally(e);
            }

            synchronized (this.calls) {
                this.calls.notifyAll();
            }
        }, "WinryManagerThread-" + this.contextName);
        this.newMain.start();

        while (isRunning()) {
            if (!this.calls.isEmpty()) {
                nextMainCall().run();
            } else {
                synchronized (this.calls) {
                    try {
                        this.calls.wait();
                    } catch (InterruptedException ignored) { }
                }
            }

            if (mainFuture.isDone() && mainFuture.isCompletedExceptionally() && error.get() != null) {
                throw Exceptions.rethrow(error.get());
            }
        }

        return ((CompletableFuture<T>) mainFuture).get();
    }

    @Override
    public Runnable nextMainCall() {
        return this.calls.poll();
    }

    @Override
    public void shutdown() {
        AraragiLogger.create("WinryAsyncManager|" + this.contextName).info("Shutting down...");
        this.running.set(false);
    }
}
