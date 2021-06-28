package tv.isshoni.winry.test;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.internal.bootstrap.SimpleBootstrapper;

import java.util.Map;

public class TestBootstrapper extends SimpleBootstrapper {

    private static TestBootstrapper instance;

    public static TestBootstrapper getInstance() {
        return instance;
    }

    private boolean run;

    public TestBootstrapper() {
        this.run = false;

        instance = this;
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
