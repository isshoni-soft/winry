package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.runner.RunnerOrder;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.test.TestCaseService;
import tv.isshoni.winry.test.model.service.OneLastTestService;

@Bootstrap(
        loadPackage = { "tv.isshoni.winry.test.model.service" },
        manualLoad = { TestInjectedClass.class })
public class TestBootstrappedClass {

    @Logger("TestBootstrappedClass")
    private static WinryLogger LOGGER;

    @Inject
    private TestInjectedClass injectedClass;

    @Inject
    private TestCaseService testService;

    @Inject
    private OneLastTestService oneLastService;

    @Runner(RunnerOrder.ASAP)
    public void asapRun() {
        LOGGER.info("ASAP RUN");

        if (this.injectedClass != null) {
            this.testService.fail("Default injected class is not null!");
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

    @Runner(RunnerOrder.LAST)
    public void lastRun() {
        LOGGER.info("LAST!!!");
        LOGGER.info("Running test: " + this.injectedClass.getNumCalled());
    }
}
