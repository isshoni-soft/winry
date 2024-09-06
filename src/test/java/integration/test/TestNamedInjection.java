package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.DummyService;
import model.integration.service.NestedService;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Named Injection",
        loader = @Loader(
                manualLoad = {DummyService.class, NestedService.class}
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestNamedInjection {

    @Inject private TestService testService;
    @Inject private NestedService nestedService;
    @Inject("2") private NestedService nestedService2;

    @Listener(WinryInitEvent.class)
    public void onInit() {
        this.testService.run();

        if (this.nestedService != null && this.nestedService.getDummyService() == null) {
            this.testService.fail("First nested service doesn't have dummy service");
        } else if (this.nestedService2 != null && this.nestedService2.getDummyService() == null) {
            this.testService.fail("Created nested service doesn't have dummy service");
        }
    }
}
