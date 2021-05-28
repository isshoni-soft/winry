package tv.isshoni.winry.entity.annotation.runner;

import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.annotation.IAnnotationWeightEnum;

public enum RunnerOrder implements IAnnotationWeightEnum {
    ASAP(6),
    PRE_INIT(3),
    INIT(Runner.DEFAULT_WEIGHT),
    POST_INIT(1),
    LAST(Integer.MIN_VALUE);

    private final int weight;

    RunnerOrder(int weight) {
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
