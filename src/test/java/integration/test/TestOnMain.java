package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.transformer.OnMain;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test @OnMain",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = Level.DEBUG)
public class TestOnMain {

    @Inject private TestService testService;

    @OnMain
    @Listener(WinryInitEvent.class)
    public void onInit() {
        this.testService.run();

        if (Thread.currentThread().getId() != 1) {
            this.testService.fail("Current thread id is " + Thread.currentThread().getId() + " it should be 1!");
        }
    }
}
