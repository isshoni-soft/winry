package tv.isshoni.winry.internal.entity.bootstrap;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.bootstrap.IExecutable;

import java.util.List;

public interface IExecutableProvider {

    List<IExecutable> provideExecutables(IWinryContext context);
}
