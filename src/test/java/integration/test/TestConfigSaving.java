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
import institute.isshoni.winry.api.service.ConfigService;

@Bootstrap(
        name = "Test Config Saving",
        loader = @Loader(
                manualLoad = TestConfig.class
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestConfigSaving {

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject TestService testService, @Inject ConfigService service, @Inject TestConfig testConfig) {
        testService.run();

        if (!testConfig.getName().equals("test")) {
            testService.fail("testConfig name is not equal to test!");
        }

        testConfig.setName("test2");

        service.save(testConfig);

        TestConfig newLoad = service.load(TestConfig.class);

        if (!newLoad.getName().equals("test2")) {
            testService.fail("testConfig name is not equal to test2 after reload!");
        }

        testConfig.setName("test");

        service.save(testConfig);
    }
}
