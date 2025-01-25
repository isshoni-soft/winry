package institute.isshoni.winry.internal.model.bootstrap;

import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.bootstrap.executable.IExecutable;

import java.util.List;

public interface IExecutableProvider {

    List<IExecutable> provideExecutables(IWinryContext context);
}
