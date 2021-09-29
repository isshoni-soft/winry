package tv.isshoni.winry.test;

import static org.junit.Assert.fail;

import tv.isshoni.winry.Winry;
import tv.isshoni.winry.api.entity.context.WinryContext;
import tv.isshoni.winry.test.model.TestBootstrappedClass;

import org.junit.Test;

public class TestWinry {

    @Test
    public void testBootstrapper() {
        TestCaseService service = new TestCaseService();

        Winry.bootstrap(TestBootstrappedClass.class, service);

        if (!service.hasRun()) {
            fail("Service never ran");
        }

        WinryContext.getContextFor(service).ifPresentOrElse(context -> {
            if (!((TestBootstrapper) context.getBootstrapper()).hasRun()) {
                fail("Bootstrapper didn't run.");
            }
        }, () -> fail("Cannot get WinryContext from provided service object!"));
    }
}
