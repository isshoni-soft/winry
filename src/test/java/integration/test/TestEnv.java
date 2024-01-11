package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Env;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Env",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = Level.DEBUG)
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
