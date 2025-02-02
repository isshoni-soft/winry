package integration.test;

import model.InjectedObject;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.logging.LogLevel;
import institute.isshoni.winry.api.event.WinryInitEvent;
import institute.isshoni.winry.api.service.ObjectFactory;

import static org.junit.Assert.assertNotNull;

@Bootstrap(name = "Test ObjectFactory",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = @LogLevel(name = "DEBUG", weight = 0))
public class TestObjectFactory {

    @Inject private TestService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject ObjectFactory factory) {
        this.testService.run();

        InjectedObject obj = factory.construct(InjectedObject.class);

        assertNotNull(obj.getLogger());

        obj.getLogger().info("Magical logger!!");
    }
}
