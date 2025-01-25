package integration.test;

import institute.isshoni.araragi.logging.AraragiLogger;
import model.integration.TestBootstrapper;
import model.integration.event.TestExecutableEvent;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.annotation.logging.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IEventBus;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Backload",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestBackload {

    @Logger("TestBackload") private AraragiLogger logger;

    @Inject private TestService testService;

    private volatile boolean backlogged = false;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject IEventBus eventBus, @Context IWinryContext context, @Inject TestService testService) throws InterruptedException {
        this.testService.run();

        this.logger.info("Registering executable...");
        eventBus.provideExecutable(context, TestExecutableEvent.class);

        this.logger.info("Triggering backload...");
        context.backload();
        this.logger.info("Checking backlogged...");
        if (!this.backlogged) {
            this.logger.error("Backlogged is false!");
            testService.fail("Backlog has not happened!");
        }
    }

    @Listener(TestExecutableEvent.class)
    public void onTestEvent() {
        this.logger.info("This must happen first now.");
        this.backlogged = true;
    }
}
