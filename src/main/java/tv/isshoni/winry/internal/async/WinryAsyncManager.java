package tv.isshoni.winry.internal.async;

import institute.isshoni.araragi.concurrent.async.AsyncManager;
import institute.isshoni.araragi.exception.Exceptions;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WinryAsyncManager extends AsyncManager implements IWinryAsyncManager {

    private final String contextName;

    private Thread newMain;

    private final BlockingQueue<Runnable> calls;

    private final AtomicBoolean running;

    private CompletableFuture<?> mainFuture;

    private final AraragiLogger logger;

    public WinryAsyncManager(Bootstrap bootstrap, ILoggerFactory loggerFactory) {
        super();

        this.contextName = bootstrap.name();
        this.logger = loggerFactory.createLogger("AsyncManager-" + this.contextName);
        this.running = new AtomicBoolean(false);
        this.calls = new LinkedBlockingQueue<>();
    }

    @Override
    public Thread newManagedThread(Runnable runnable, String name) {
        if (this.managedThreads.containsKey(name)) {
            throw new IllegalStateException("Cannot overwrite pre-existing managed thread!");
        }

        Thread thread = new Thread(runnable, name);
        thread.setUncaughtExceptionHandler((th, t) -> submitToMain((Callable<?>) () -> {
            if (t instanceof Exception e) {
                throw e;
            } else {
                throw new Exception(t);
            }
        }));

        this.managedThreads.put(name, thread);
        return thread;
    }

    public void joinManagedThread(String name) throws InterruptedException {
        if (!this.managedThreads.containsKey(name)) {
            throw new IllegalArgumentException(name + " is not a managed thread!");
        }

        Thread found = getManagedThread(name);

        if (this.newMain.equals(found)) {
            throw new IllegalStateException("cannot join winry manager thread!");
        }

        found.join();
        this.managedThreads.remove(name);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        this.logger.debug("Submitting call to executor...");
        return super.submit(callable);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        this.logger.debug("Submitting call to executor...");
        return super.submit(runnable);
    }

    @Override
    public boolean isRunning() {
        return this.newMain != null && !this.calls.isEmpty() || this.running.get();
    }

    @Override
    public <T> Future<T> submitToMain(Callable<T> callable) {
        this.logger.debug("Submitting call to main thread...");
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
        }

        return future;
    }

    @Override
    public Future<?> submitToMain(Runnable runnable) {
        this.logger.debug("Submitting call to main thread...");
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
        }

        return future;
    }

    @Override
    public <T> T forkMain(Callable<T> cont) throws ExecutionException, InterruptedException {
        this.logger.info("Forking thread with id: ${0}", Thread.currentThread().getId());
        this.running.set(true);
        this.mainFuture = new CompletableFuture<T>();
        AtomicReference<Throwable> error = new AtomicReference<>(null);
        this.newMain = newManagedThread(() -> {
            try {
                ((CompletableFuture<T>) mainFuture).complete(cont.call());
            } catch (Throwable e) {
                error.set(e);
                mainFuture.completeExceptionally(e);
            }
        }, "WinryManagerThread-" + this.contextName);
        this.newMain.start();

        while (isRunning()) {
            if (!this.calls.isEmpty()) {
                nextMainCall().run();
            }

            if (mainFuture.isDone() && mainFuture.isCompletedExceptionally() && error.get() != null) {
                throw Exceptions.rethrow(error.get());
            }
        }

        return ((CompletableFuture<T>) mainFuture).get();
    }

    @Override
    public Runnable nextMainCall() throws InterruptedException {
        return this.calls.take();
    }

    @Override
    public Runnable nextMainCall(long timeout, TimeUnit unit) throws InterruptedException {
        return this.calls.poll(timeout, unit);
    }

    @Override
    public void shutdown() {
        AraragiLogger.create("WinryAsyncManager|" + this.contextName).info("Shutting down...");
        this.running.set(false);
    }
}
