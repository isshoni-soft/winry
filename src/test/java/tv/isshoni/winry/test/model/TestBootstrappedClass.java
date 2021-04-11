package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.runner.RunnerOrder;
import tv.isshoni.winry.logging.WinryLogger;

@Bootstrap
public class TestBootstrappedClass {

    @Logger("TestBootstrappedClass")
    private static WinryLogger LOGGER;

    @Runner(RunnerOrder.ASAP)
    public void asapRun() {
        LOGGER.info("ASAP RUN");
    }

    @Runner
    public void initRun() {
        LOGGER.info("Init run");
    }
}
