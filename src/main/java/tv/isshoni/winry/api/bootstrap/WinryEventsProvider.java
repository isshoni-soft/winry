package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.event.WinryPostInitEvent;
import tv.isshoni.winry.api.event.WinryPreInitEvent;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.internal.model.bootstrap.IExecutableProvider;

import java.util.List;

public class WinryEventsProvider implements IExecutableProvider {

    @Override
    public List<IExecutable> provideExecutables(IWinryContext context) {
        context.getEventBus().registerExecutable(WinryPreInitEvent.class, 500000);
        context.getEventBus().registerExecutable(WinryInitEvent.class, 90000);
        context.getEventBus().registerExecutable(WinryPostInitEvent.class, 50000);
        context.getEventBus().registerExecutable(WinryShutdownEvent.class, Integer.MIN_VALUE);

        return null;
    }
}
