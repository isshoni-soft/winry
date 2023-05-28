package tv.isshoni.winry.api.bootstrap.executable;

import tv.isshoni.winry.internal.model.meta.IWeighted;

public interface IExecutable extends IWeighted {

    void execute();

    String getDisplay();
}
