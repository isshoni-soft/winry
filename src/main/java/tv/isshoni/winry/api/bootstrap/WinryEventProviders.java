package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.event.WinryEventExecutable;
import tv.isshoni.winry.api.entity.executable.IExecutable;
import tv.isshoni.winry.api.event.WinryInitEvent;
import tv.isshoni.winry.api.event.WinryPostInitEvent;
import tv.isshoni.winry.api.event.WinryPreInitEvent;
import tv.isshoni.winry.api.event.WinryShutdownEvent;
import tv.isshoni.winry.entity.bootstrap.IExecutableProvider;

import java.util.LinkedList;
import java.util.List;

public class WinryEventProviders implements IExecutableProvider {

    @Override
    public List<IExecutable> provideExecutables(IWinryContext context) {
        List<IExecutable> result = new LinkedList<>();

        result.add(new WinryEventExecutable<>(WinryPreInitEvent.class, 500000, context));
        result.add(new WinryEventExecutable<>(WinryInitEvent.class, 90000, context));
        result.add(new WinryEventExecutable<>(WinryPostInitEvent.class, 5000, context));
        result.add(new WinryEventExecutable<>(WinryShutdownEvent.class, Integer.MIN_VALUE, context));

        return result;
    }
}
