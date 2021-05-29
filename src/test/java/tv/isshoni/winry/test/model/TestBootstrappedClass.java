package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.Runner;
import tv.isshoni.winry.entity.annotation.runner.RunnerOrder;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.test.TestBootstrapper;
import tv.isshoni.winry.test.TestCaseService;
import tv.isshoni.winry.test.model.service.OneLastTestService;

import static org.junit.Assert.assertEquals;

@Bootstrap(
        bootstrapper = TestBootstrapper.class,
        loadPackage = { "tv.isshoni.winry.test.model.service" },
        manualLoad = { TestInjectedClass.class })
public class TestBootstrappedClass {

    @Logger(value = "TestBootstrappedClass") private static WinryLogger LOGGER;

    @Inject private TestInjectedClass injectedClass;
    @Inject private TestCaseService testService;
    @Inject private OneLastTestService oneLastService;

    @Runner(RunnerOrder.ASAP)
    public void asapRun() {
        LOGGER.info("Logger test!");

        if (this.injectedClass != null) {
            this.testService.fail("Default injected class is not null!");
        }
    }

    @Runner
    public void initRun() {
        assertEquals(0, this.injectedClass.getNumCalled());
        assertEquals(5, this.injectedClass.getTest());
    }

    @Runner(RunnerOrder.POST_INIT)
    public void postInitRun() {
        assertEquals(1, this.injectedClass.getNumCalled());
        assertEquals(2, this.oneLastService.getInjectedClassVal());
    }

    @Runner(RunnerOrder.LAST)
    public void lastRun() {
        assertEquals(3, this.injectedClass.getNumCalled());
    }
}
