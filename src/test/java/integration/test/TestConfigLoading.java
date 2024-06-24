package integration.test;

import model.config.TestConfig;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.event.WinryInitEvent;

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

        if (!testConfig.getName().equals("test")) {
            testService.fail("testConfig name is not equal to test!");
        }
    }
}
