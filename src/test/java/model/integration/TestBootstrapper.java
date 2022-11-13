package model.integration;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.bootstrap.WinryBootstrapper;
import tv.isshoni.winry.api.async.IWinryAsyncManager;

import java.util.Map;

public class TestBootstrapper extends WinryBootstrapper {

    private boolean run;

    public TestBootstrapper(Bootstrap bootstrap, IWinryAsyncManager asyncManager) {
        super(bootstrap, asyncManager);

        this.run = false;
    }

    public boolean hasRun() {
        return this.run;
    }

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        this.run = true;

        super.bootstrap(bootstrap, clazz, provided);
    }
}
