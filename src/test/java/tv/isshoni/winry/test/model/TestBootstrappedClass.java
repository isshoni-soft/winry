package tv.isshoni.winry.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.event.WinryPostInitEvent;
import tv.isshoni.winry.api.event.WinryPreInitEvent;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.test.TestBootstrapper;
import tv.isshoni.winry.test.TestCaseService;
import tv.isshoni.winry.test.model.service.OneLastTestService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Bootstrap(
        bootstrapper = TestBootstrapper.class,
        loadPackage = { "tv.isshoni.winry.test.model.service" },
        manualLoad = { TestInjectedClass.class },
        defaultLevel = Level.DEBUG)
public class TestBootstrappedClass {

    @Logger("TestBootstrappedClass") private static AraragiLogger LOGGER;

    @Inject private TestInjectedClass injectedClass;
    @Inject("Second") private TestInjectedClass secondInjectedClass;
    @Inject private TestCaseService testService;
    @Inject private OneLastTestService oneLastService;

    @Listener(WinryPreInitEvent.class)
    public void preInitRun() {
        LOGGER.info("Logger test!");

        if (this.injectedClass != null) {
            this.testService.fail("Default injected class is not null!");
        }
    }

    @Listener(WinryInitEvent.class)
    public void initRun() {
        this.injectedClass.asyncMethod();

        assertEquals(0, this.injectedClass.getNumCalled());
        assertEquals(5, this.injectedClass.getTest());

        this.injectedClass.testProfiling();
    }

    @Listener(WinryPostInitEvent.class)
    public void postInitRun(@Context IWinryContext context) {
        assertNotNull(context);

        LOGGER.info("Run context: " + context);

        assertEquals(1, this.injectedClass.getNumCalled());
        assertEquals(2, this.oneLastService.getInjectedClassVal());
        assertEquals(0, this.secondInjectedClass.getNumCalled());
    }

    @Listener(WinryShutdownEvent.class)
    public void lastRun() {
        assertEquals(3, this.injectedClass.getNumCalled());

        try {
            Future<Integer> future = this.injectedClass.asyncFutureMethod();

            Thread.sleep(10);

            LOGGER.info("Selected num was " + this.injectedClass.getSelectedNum());

            for (int x = 0; x < 10; x++) {
                LOGGER.info(String.valueOf(x));
            }

            LOGGER.info("Waiting on async future method...");
            assertEquals(this.injectedClass.getSelectedNum(), future.get().intValue());
            LOGGER.info("Async method matching expected values!");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        this.testService.run();
    }
}
