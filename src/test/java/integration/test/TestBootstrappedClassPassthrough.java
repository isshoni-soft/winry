package integration.test;

import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;

@Bootstrap(name = "Test Bootstrapped Class Passthrough",
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestBootstrappedClassPassthrough {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Context IWinryContext context) {
        testService.run();

        if (!context.getBootstrapContext().getBootstrappedClass().equals(TestBootstrappedClassPassthrough.class)) {
            testService.fail("context.getBootstrapContext().getBootstrappedClass() != TestBootstrappedClassPassthrough");
        }
    }
}
