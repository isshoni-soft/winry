package tv.isshoni.winry.test;

import org.junit.Test;
import tv.isshoni.winry.Winry;
import tv.isshoni.winry.test.model.TestBootstrappedClass;

import static org.junit.Assert.fail;

public class TestWinry {

    @Test
    public void testBootstrapper() {
        TestCaseService service = new TestCaseService();

        Winry.bootstrap(TestBootstrappedClass.class, service);

        if (!((TestBootstrapper) Winry.getBootstrapper()).hasRun()) {
            fail("Bootstrapper didn't run.");
        }
    }
}
