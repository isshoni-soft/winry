package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.winry.api.bootstrap.executable.IExecutable;
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
        context.getEventBus().provideExecutable(context, WinryPreInitEvent.class);
        context.getEventBus().provideExecutable(context, WinryInitEvent.class);
        context.getEventBus().provideExecutable(context, WinryPostInitEvent.class);
        context.getEventBus().provideExecutable(context, WinryShutdownEvent.class);

        return null;
    }
}
