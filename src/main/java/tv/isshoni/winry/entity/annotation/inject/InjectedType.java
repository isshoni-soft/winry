package tv.isshoni.winry.entity.annotation.inject;

import tv.isshoni.araragi.annotation.model.IAnnotationWeightEnum;
import tv.isshoni.winry.api.annotation.Injected;

public enum InjectedType implements IAnnotationWeightEnum {
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