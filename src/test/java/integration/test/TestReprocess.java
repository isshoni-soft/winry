package integration.test;

import model.annotation.Reinject;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;

import static junit.framework.TestCase.assertEquals;

@Bootstrap(name = "Reprocess",
        loader = @Loader(
                manualLoad = { Reinject.class }
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = Level.DEBUG
)
public class TestReprocess {

    @Reinject
    private Integer num;

    @Listener(WinryInitEvent.class)
    public void onInit(@Context IWinryContext context, @Inject TestService service) {
        assertEquals(0, num.intValue());
        context.reprocess(Reinject.class);
        assertEquals(1, num.intValue());

        if (num != 1) {
            service.fail("num is not equal to 1!");
        }

        service.run();
    }
}
