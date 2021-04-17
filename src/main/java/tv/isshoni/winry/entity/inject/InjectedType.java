package tv.isshoni.winry.entity.inject;

import tv.isshoni.winry.annotation.Injected;

public enum InjectedType {
    DEFAULT(Injected.DEFAULT_WEIGHT),
    DATABASE(7),
    LOGGER(9);

    private final int weight;

    InjectedType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
