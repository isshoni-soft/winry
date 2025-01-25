package institute.isshoni.winry.api.bootstrap;

import institute.isshoni.winry.api.bootstrap.executable.IExecutable;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.event.WinryInitEvent;
import institute.isshoni.winry.api.event.WinryPostInitEvent;
import institute.isshoni.winry.api.event.WinryPreInitEvent;
import institute.isshoni.winry.api.event.WinryShutdownEvent;
import institute.isshoni.winry.internal.model.bootstrap.IExecutableProvider;

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
