package tv.isshoni.winry.internal.event;

import tv.isshoni.winry.internal.model.event.IEventHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class WinryLambdaEventHandler<T> implements IEventHandler<T> {

    private final Consumer<T> handler;

    private final Class<T> type;

    private final int weight;

    private final boolean shouldIgnoreCancelled;
    private final boolean requireExact;

    private final UUID id;

    public WinryLambdaEventHandler(Consumer<T> handler, Class<T> type, int weight, boolean shouldIgnoreCancelled, boolean requireExact) {
        this.id = UUID.randomUUID();
        this.handler = handler;
        this.type = type;
        this.weight = weight;
        this.shouldIgnoreCancelled = shouldIgnoreCancelled;
        this.requireExact = requireExact;
    }

    public WinryLambdaEventHandler(Consumer<T> handler, Class<T> type, int weight) {
        this(handler, type, weight, false, false);
    }

    @Override
    public UUID getId() {
        return this.id;
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
    public boolean requiresExact() {
        return this.requireExact;
    }
}
