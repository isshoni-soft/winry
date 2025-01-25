package integration.test;

import institute.isshoni.araragi.logging.AraragiLogger;
import model.integration.TestBootstrapper;
import model.integration.event.SuperTestEvent;
import model.integration.event.TestEvent;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.annotation.logging.Logger;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.exception.EventExecutionException;

@Bootstrap(name = "Test Event Prioritize Exact",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestEventPrioritizeExact {

    @Logger private AraragiLogger logger;

    @Inject private TestService testService;

    private boolean ran = false;

    @Listener(WinryInitEvent.class)
    public void init(@Inject IEventBus eventBus) throws EventExecutionException {
        this.testService.run();

        eventBus.fire(SuperTestEvent.class);
    }

    @Listener(value = SuperTestEvent.class)
    public void onSuperTestEvent(@Event SuperTestEvent event) {
        if (ran) {
            testService.fail("Super Event: Ran should be false!");
        }
    }

    @Listener(value = TestEvent.class)
    public void onTestEvent(@Event TestEvent event) {
        if (ran) {
            testService.fail("Sub Event: Ran should be false!");
        }

        ran = true;
    }
}
