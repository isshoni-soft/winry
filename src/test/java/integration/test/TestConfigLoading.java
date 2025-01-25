package integration.test;

import model.config.TestConfig;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.Loader;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(
        name = "Test Config Loading",
        loader = @Loader(
                manualLoad = TestConfig.class
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestConfigLoading {

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject TestService testService, @Inject TestConfig testConfig) {
        testService.run();

        if (!testConfig.getName().equals("test") && !testConfig.getName().equals("test2")) {
            testService.fail("testConfig name is not equal to test or test2!");
        }
    }
}
