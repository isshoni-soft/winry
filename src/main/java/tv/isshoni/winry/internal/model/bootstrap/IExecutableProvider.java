package tv.isshoni.winry.internal.model.bootstrap;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.bootstrap.executable.IExecutable;

import java.util.List;

public interface IExecutableProvider {

    List<IExecutable> provideExecutables(IWinryContext context);
}
