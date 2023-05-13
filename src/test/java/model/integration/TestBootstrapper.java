package model.integration;

import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.bootstrap.WinryBootstrapper;
import tv.isshoni.winry.api.context.IBootstrapContext;

import java.util.Map;

public class TestBootstrapper extends WinryBootstrapper {

    private boolean run;

    private final IBootstrapContext bootstrapContext;

    public TestBootstrapper(Bootstrap bootstrap, IBootstrapContext bootstrapContext) {
        super(bootstrap, bootstrapContext);

        this.bootstrapContext = bootstrapContext;
        this.run = false;
    }

    public IBootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
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
