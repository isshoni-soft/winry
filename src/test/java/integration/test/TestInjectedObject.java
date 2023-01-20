package integration.test;

import model.InjectedObject;
import model.integration.TestBootstrapper;
import model.integration.TestCaseService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.service.ObjectFactory;

import static org.junit.Assert.assertNotNull;

@Bootstrap(name = "Test Injected Objects",
           bootstrapper = TestBootstrapper.class,
           defaultLevel = Level.DEBUG)
public class TestInjectedObject {

    @Inject private TestCaseService testService;

    @Listener(WinryInitEvent.class)
    public void onInit(@Inject ObjectFactory factory) {
        this.testService.run();

        InjectedObject obj = factory.construct(InjectedObject.class);

        assertNotNull(obj.getLogger());

        obj.getLogger().info("Magical logger!!");
    }
}
