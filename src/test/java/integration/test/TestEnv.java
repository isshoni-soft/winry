package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Env;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Env",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestEnv {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Env("TEST") String test) {
        this.testService.run();

        if (!test.equals("true")) {
            this.testService.fail("TEST environment variable is not true");
        }
    }
}
