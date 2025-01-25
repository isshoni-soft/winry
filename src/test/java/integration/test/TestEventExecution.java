package integration.test;

import institute.isshoni.araragi.logging.AraragiLogger;
import model.integration.TestBootstrapper;
import model.integration.event.SuperTestEvent;
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

@Bootstrap(name = "Test Event Execution",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestEventExecution {

    @Logger
    private AraragiLogger logger;

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void init(@Inject IEventBus eventBus) throws EventExecutionException {
        this.testService.run();

        TestEvent event = eventBus.fire(TestEvent.class);

        if (event.getData() != 55) {
            this.testService.fail("Event data is not 55");
        }
    }

    @Listener(value = TestEvent.class)
    public void onSuperTestEvent(@Event TestEvent event) {
        event.setData(55);
    }

    @Listener(value = SuperTestEvent.class)
    public void onTestEvent(@Event SuperTestEvent event) {
        this.testService.fail("Parent handler should not run!");
    }
}
