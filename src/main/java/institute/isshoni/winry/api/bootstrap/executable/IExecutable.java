package institute.isshoni.winry.api.bootstrap.executable;

import institute.isshoni.winry.internal.model.meta.IWeighted;

public interface IExecutable extends IWeighted {

    void execute();

    String getDisplay();
}
