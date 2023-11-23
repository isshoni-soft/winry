package integration.test;

import model.EventObject;
import model.integration.TestBootstrapper;
import model.integration.event.TestEvent;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.exception.EventExecutionException;
import tv.isshoni.winry.api.service.ObjectFactory;

@Bootstrap(
        name = "Test Object Level Events",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = Level.DEBUG
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

        eventObject.close();
        eventBus.fire(new TestEvent(1));

        if (eventObject.hasReceivedEvent() == 2) {
            testService.fail("Event object has run more than once!");
        }
    }
}
