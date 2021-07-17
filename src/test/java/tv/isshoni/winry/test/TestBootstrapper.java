package tv.isshoni.winry.test;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.internal.bootstrap.SimpleBootstrapper;

import java.util.Map;

public class TestBootstrapper extends SimpleBootstrapper {

    private boolean run;

    public TestBootstrapper() {
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
