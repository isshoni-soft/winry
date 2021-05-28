package tv.isshoni.winry.entity.annotation.inject;

import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.entity.annotation.AnnotationWeightEnum;

public enum InjectedType implements AnnotationWeightEnum {
    DEFAULT(Injected.DEFAULT_WEIGHT),
    DATABASE(7),
    LOGGER(9);

    private final int weight;

    InjectedType(int weight) {
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
