package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.AsyncTester;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.Loader;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test @OnMain",
        loader = @Loader(
                manualLoad = {AsyncTester.class}
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestOnMain {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject AsyncTester asyncTester) {
        this.testService.run();

        asyncTester.onMainMethod(this.testService);
    }
}
