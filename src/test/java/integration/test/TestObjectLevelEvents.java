package integration.test;

import model.EventObject;
import model.integration.TestBootstrapper;
import model.integration.event.TestEvent;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.context.IEventBus;
import institute.isshoni.winry.api.event.WinryInitEvent;
import institute.isshoni.winry.api.exception.EventExecutionException;
import institute.isshoni.winry.api.service.ObjectFactory;

@Bootstrap(
        name = "Test Object Level Events",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestObjectLevelEvents {

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject IEventBus eventBus, @Inject ObjectFactory objFactory, @Inject TestService testService) throws EventExecutionException {
        testService.run();

        EventObject eventObject = objFactory.construct(EventObject.class);
        eventBus.fire(new TestEvent(1));

        if (eventObject.hasReceivedEvent() != 1) {
            testService.fail("Event object has not run!");
        }

        eventBus.fire(new TestEvent(1));

        if (eventObject.hasReceivedEvent() != 2) {
            testService.fail("Event object has only run once!");
        }

        eventObject.close();

        eventBus.fire(new TestEvent(1));
        if (eventObject.hasReceivedEvent() != 2) {
            testService.fail("Event object has run more than twice!");
        }

        eventObject.reregister();
        eventBus.fire(new TestEvent(1));

        if (eventObject.hasReceivedEvent() != 3) {
            testService.fail("Event object has not run thrice!");
        }
    }
}
