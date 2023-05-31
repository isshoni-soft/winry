package integration.test;

import model.integration.TestBootstrapper;
import model.integration.TestService;
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

    @Inject private TestService testService;

    private volatile boolean backlogged = false;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject IEventBus eventBus, @Inject IWinryContext context, @Inject TestService testService) throws InterruptedException {
        this.testService.run();

        this.logger.info("Registering executable...");
        eventBus.provideExecutable(context, TestExecutableEvent.class);

        this.logger.info("Triggering backload...");
        context.backload();
        Thread.sleep(1000);
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
