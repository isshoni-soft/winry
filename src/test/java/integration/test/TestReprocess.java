package integration.test;

import model.annotation.Reinject;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.Loader;
import tv.isshoni.winry.api.annotation.logging.LogLevel;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;

import static junit.framework.TestCase.assertEquals;

@Bootstrap(name = "Reprocess",
        loader = @Loader(
                manualLoad = { Reinject.class }
        ),
        bootstrapper = TestBootstrapper.class,
        defaultLevel = @LogLevel(name = "DEBUG", weight = 0)
)
public class TestReprocess {

    @Reinject
    private Integer num;

    @Listener(WinryInitEvent.class)
    public void onInit(@Context IWinryContext context, @Inject TestService service) {
        assertEquals(0, num.intValue());
        context.reprocess(Reinject.class);
        assertEquals(1, num.intValue());

        service.run();
    }
}
