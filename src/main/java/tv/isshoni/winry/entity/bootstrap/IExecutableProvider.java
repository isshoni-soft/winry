package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.api.entity.context.IWinryContext;
import tv.isshoni.winry.api.entity.executable.IExecutable;

import java.util.List;

public interface IExecutableProvider {

    List<IExecutable> provideExecutables(IWinryContext context);
}
