package tv.isshoni.winry.entity.runner;

// TODO: Make this extendable instead of an enum, allowing people to register their own types w/ independent weights
public enum RunnerOrder {
    ASAP,
    PRE_INIT,
    INIT,
    POST_INIT,
    LAST
}
