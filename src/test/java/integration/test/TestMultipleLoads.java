package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.AsyncTester;
import model.integration.service.DummyService;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Multiple Manual Loads",
        loader = @Loader(
                manualLoad = {AsyncTester.class, DummyService.class}
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestMultipleLoads {

    @Inject private AsyncTester asyncTester;
    @Inject private DummyService dummyService;
    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit() {
        this.testService.run();

        if (this.asyncTester == null) {
            this.testService.fail("asyncTester is null!");
        }

        if (this.dummyService == null) {
            this.testService.fail("dummyService is null!");
        }
    }
}
