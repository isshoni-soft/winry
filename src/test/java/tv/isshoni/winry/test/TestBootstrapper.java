package tv.isshoni.winry.test;

import org.junit.Test;
import tv.isshoni.winry.Winry;
import tv.isshoni.winry.test.model.TestBootstrappedClass;

public class TestBootstrapper {

    @Test
    public void testBootstrapper() {
        Winry.bootstrap(TestBootstrappedClass.class);
    }
}
