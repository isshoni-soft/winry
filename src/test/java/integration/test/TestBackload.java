package integration.test;

import model.integration.TestBootstrapper;
import model.integration.TestCaseService;
import model.integration.event.TestExecutableEvent;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Backload",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = Level.DEBUG)
public class TestBackload {

    @Logger("TestBackload") private AraragiLogger logger;

    @Inject
    private TestCaseService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject IEventBus eventBus, @Inject IWinryContext context) {
        this.testService.run();

        this.logger.info("Registering executable...");
        eventBus.provideExecutable(context, TestExecutableEvent.class);

        this.logger.info("Triggering backload...");
        context.backload();
    }
}
