package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.runner.RunnerOrder;
import tv.isshoni.winry.logging.WinryLogger;

@Bootstrap
public class TestBootstrappedClass {

    @Logger("TestBootstrappedClass")
    private static WinryLogger LOGGER;

    @Inject
    private TestInjectedClass injectedClass;

    @Runner(RunnerOrder.ASAP)
    public void asapRun() {
        LOGGER.info("ASAP RUN");

        if (this.injectedClass == null) {
            LOGGER.info("Expected outcome!");
        } else {
            LOGGER.severe("Unexpected outcome!");
        }
    }

    @Runner
    public void initRun() {
        LOGGER.info("Init run");
        LOGGER.info("Running test: " + this.injectedClass.getNumCalled());
    }

    @Runner(RunnerOrder.POST_INIT)
    public void postInitRun() {
        LOGGER.info("Post-init run");
        LOGGER.info("Running test: " + this.injectedClass.getNumCalled());
    }
}
