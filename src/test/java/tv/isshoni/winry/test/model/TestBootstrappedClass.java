package tv.isshoni.winry.test.model;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.annotation.parameter.Event;
import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.event.WinryPostInitEvent;
import tv.isshoni.winry.api.event.WinryPreInitEvent;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.test.TestBootstrapper;
import tv.isshoni.winry.test.TestCaseService;
import tv.isshoni.winry.test.event.TestEvent;
import tv.isshoni.winry.test.model.service.OneLastTestService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Bootstrap(
        bootstrapper = TestBootstrapper.class,
        loadPackage = { "tv.isshoni.winry.test.model.service" },
        manualLoad = { TestInjectedClass.class },
        defaultLevel = Level.DEBUG)
public class TestBootstrappedClass {

    @Logger("TestBootstrappedClass") private static AraragiLogger LOGGER;

    @Inject private TestInjectedClass injectedClass;
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
    public void initRun(@Event WinryInitEvent event, @Context IWinryContext context) {
        assertNotNull(event);
        this.injectedClass.asyncMethod();

        TestEvent fired = context.getEventBus().fire(new TestEvent(5));
        assertEquals(10, fired.getData());

        assertEquals(0, this.injectedClass.getNumCalled());
        assertEquals(5, this.injectedClass.getTest());

        this.injectedClass.testProfiling();
    }

    @Listener(TestEvent.class)
    public void onTestEvent(@Event TestEvent event) {
        LOGGER.info("Test Event: " + event.getData());

        event.setData(event.getData() * 2);
    }

    @Listener(WinryPostInitEvent.class)
    public void postInitRun(
            @Inject("Second") TestInjectedClass secondInjectedClass,
            @Context IWinryContext context,
            @Event WinryPostInitEvent event) {
        assertNotNull(context);
        assertNotNull(event);
        assertNotNull(secondInjectedClass);

        LOGGER.info("Run context: " + context);

        assertEquals(1, this.injectedClass.getNumCalled());
        assertEquals(2, this.oneLastService.getInjectedClassVal());
        assertEquals(0, secondInjectedClass.getNumCalled());
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
