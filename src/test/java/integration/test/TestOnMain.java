package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.AsyncTester;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test @OnMain",
        loader = @Loader(
                manualLoad = {AsyncTester.class}
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = Level.DEBUG)
public class TestOnMain {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject AsyncTester asyncTester) {
        this.testService.run();

        asyncTester.onMainMethod(this.testService);
    }
}
