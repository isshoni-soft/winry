package tv.isshoni.winry.entity.runner;

import tv.isshoni.winry.annotation.Runner;

public enum RunnerOrder {
    ASAP(4),
    PRE_INIT(3),
    INIT(Runner.DEFAULT_WEIGHT),
    POST_INIT(1),
    LAST(0);

    private final int weight;

    RunnerOrder(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
