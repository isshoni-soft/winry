package integration.test;

import model.integration.TestBootstrapper;
import model.integration.TestService;
import model.service.AsyncTester;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test @Async",
        loader = @Loader(
                manualLoad = { AsyncTester.class }
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = Level.DEBUG
)
public class TestAsync {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject AsyncTester tester) {
        this.testService.run();

        tester.asyncMethod(Thread.currentThread().getId(), this.testService);
    }
}
