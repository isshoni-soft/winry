package tv.isshoni.winry.entity.inject;

// TODO: Make this extendable instead of an enum, allowing people to register their own types w/ independent weights
public enum InjectedType {
    DEFAULT,
    DATABASE,
    SERVICE,
    LOGGER
}
