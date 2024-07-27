package integration.test;

import model.integration.TestBootstrapper;
import model.integration.event.SuperTestEvent;
import model.integration.event.TestEvent;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Event;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.annotation.logging.Logger;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.exception.EventExecutionException;

@Bootstrap(name = "Test Event RequireExact",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestEventRequireExact {

    @Logger
    private AraragiLogger logger;

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void init(@Inject IEventBus eventBus) throws EventExecutionException {
        this.testService.run();

        SuperTestEvent event = eventBus.fire(SuperTestEvent.class);

        if (event.getData() != 50) {
            this.testService.fail("Event data is not 50!");
        }
    }

    @Listener(value = SuperTestEvent.class)
    public void onSuperTestEvent(@Event SuperTestEvent event) {
        event.setData(50);
    }

    @Listener(value = TestEvent.class, requireExact = true)
    public void onTestEvent(@Event TestEvent event) {
        this.testService.fail("RequireExact handler should not run!");
    }
}
