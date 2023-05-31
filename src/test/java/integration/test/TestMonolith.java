package integration.test;

import model.exception.TestException;
import model.exception.TestExceptionHandler;
import model.integration.TestBootstrapper;
import model.integration.TestCaseService;
import model.integration.event.TestEvent;
import model.integration.model.TestInjectedClass;
import model.service.OneLastTestService;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.exception.ExceptionHandler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.async.AsyncService;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.event.WinryPostInitEvent;
import tv.isshoni.winry.api.event.WinryPreInitEvent;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.api.service.VersionService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// TODO: Replace test monolith with specific targeted tests for more precise failure reporting.
@Deprecated
@Bootstrap(
        name = "Winry Testing",
        bootstrapper = TestBootstrapper.class,
        loader = @Loader(
                loadPackage = { "model.service", "model.exception" },
                manualLoad = { TestInjectedClass.class }
        ),
        defaultLevel = Level.DEBUG)
public class TestMonolith {

    @Logger("TestBootstrappedClass") private static AraragiLogger LOGGER;

    @Inject private TestInjectedClass injectedClass;
    @Inject private TestCaseService testService;
    @Inject private OneLastTestService oneLastService;

    @Inject private AsyncService asyncService;

    @Listener(WinryPreInitEvent.class)
    public void preInitRun(@Inject VersionService service) {
        LOGGER.info("Testing Winry v" + service.getWinryVersion());

        if (this.injectedClass != null) {
            this.testService.fail("Default injected class is not null!");
        }
    }

    @Listener(WinryInitEvent.class)
    public void initRun(@Event WinryInitEvent event, @Context IWinryContext context) {
        assertNotNull(event);
        this.injectedClass.asyncMethod();

        TestEvent fired;
        try {
            fired = context.getEventBus().fire(new TestEvent(5));
        } catch (EventExecutionException e) {
            fail();
            return;
        }

        assertEquals(10, fired.getData());

        assertEquals(0, this.injectedClass.getNumCalled());
        assertEquals(5, this.injectedClass.getTest());

        this.injectedClass.testProfiling();

        LOGGER.info("Current thread id is: " + Thread.currentThread().getId() + " testing if main thread runner works...");
        this.asyncService.onMain(() -> {
            if (Thread.currentThread().getId() != 1) {
                fail("AsyncService.onMain not running on thread id 1");
            }
        });
    }

    @Listener(TestEvent.class)
    @ExceptionHandler(TestExceptionHandler.class)
    public void onTestEvent(@Event TestEvent event) {
        LOGGER.info("Test Event: " + event.getData());

        event.setData(event.getData() * 2);

        throw new TestException();
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

        assertTrue(context.getExceptionManager()
                .getSingleton(TestExceptionHandler.class)
                .map(TestExceptionHandler::hasRun).orElse(false));
    }

    @Listener(WinryShutdownEvent.class)
    public void lastRun(@Inject("Second") TestInjectedClass secondInjectedClass) {
        LOGGER.info("Starting lastRun...");
        assertEquals(3, this.injectedClass.getNumCalled());
        assertEquals(1, secondInjectedClass.getNumCalled());

        try {
            LOGGER.info("Starting async future methods...");
            Future<Integer> future = this.injectedClass.asyncFutureMethod();

            Thread.sleep(10);

            LOGGER.info("Selected num was " + this.injectedClass.getSelectedNum());
            LOGGER.info("Waiting on async future method...");
            assertEquals(this.injectedClass.getSelectedNum(), future.get().intValue());
            LOGGER.info("Async method matching expected values!");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        this.testService.run();
    }
}