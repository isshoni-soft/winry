package tv.isshoni.winry.internal.event;

import tv.isshoni.winry.internal.model.event.IEventHandler;

import java.util.function.Consumer;

public class WinryLambdaEventHandler<T> implements IEventHandler<T> {

    private final Consumer<T> handler;

    private final Class<T> type;

    private final int weight;

    private final boolean shouldIgnoreCancelled;

    private final boolean needsMainThread;

    public WinryLambdaEventHandler(Consumer<T> handler, Class<T> type, int weight, boolean shouldIgnoreCancelled,
                                   boolean needsMainThread) {
        this.handler = handler;
        this.type = type;
        this.weight = weight;
        this.shouldIgnoreCancelled = shouldIgnoreCancelled;
        this.needsMainThread = needsMainThread;
    }

    public WinryLambdaEventHandler(Consumer<T> handler, Class<T> type, int weight, boolean shouldIgnoreCancelled) {
        this(handler, type, weight, shouldIgnoreCancelled, false);
    }

    public WinryLambdaEventHandler(Consumer<T> handler, Class<T> type, int weight) {
        this(handler, type, weight, false, false);
    }

    @Override
    public void execute(T event) {
        this.handler.accept(event);
    }

    @Override
    public Class<T> getTargetEvent() {
        return this.type;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public boolean shouldIgnoreCancelled() {
        return this.shouldIgnoreCancelled;
    }

    @Override
    public boolean needsMainThread() {
        return this.needsMainThread;
    }
}
