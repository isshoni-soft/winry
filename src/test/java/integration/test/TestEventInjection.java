package integration.test;

import institute.isshoni.araragi.logging.AraragiLogger;
import model.integration.TestBootstrapper;
import model.integration.event.TestEvent;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Event;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.annotation.logging.Logger;
import institute.isshoni.winry.api.context.IEventBus;
import institute.isshoni.winry.api.event.WinryInitEvent;
import institute.isshoni.winry.api.exception.EventExecutionException;

@Bootstrap(name = "Test Event",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestEventInjection {

    @Logger private AraragiLogger logger;

    @Listener(WinryInitEvent.class)
    public void init(@Inject IEventBus eventBus, @Inject TestService testService) throws EventExecutionException {
        testService.run();

        TestEvent event = eventBus.fire(TestEvent.class);
        logger.info("Event Data: ${0}", event.getData());

        if (event.getData() != 42) {
            testService.fail("Event data is not 42!");
        }
    }

    @Listener(TestEvent.class)
    public void onTestEvent(@Event TestEvent event) {
        event.setData(42);
    }
}
