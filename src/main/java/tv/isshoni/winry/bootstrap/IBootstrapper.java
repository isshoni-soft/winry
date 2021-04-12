package tv.isshoni.winry.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;

public interface IBootstrapper {

    void bootstrap(Bootstrap bootstrap, Class<?> baseClazz, Object... provided);
}
