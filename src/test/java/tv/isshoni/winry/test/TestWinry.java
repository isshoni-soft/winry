package tv.isshoni.winry.test;

import org.junit.Test;
import tv.isshoni.winry.Winry;
import tv.isshoni.winry.api.entity.context.WinryContext;
import tv.isshoni.winry.test.model.TestBootstrappedClass;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

public class TestWinry {

    @Test
    public void testBootstrapper() {
        TestCaseService service = new TestCaseService();

        try {
            Winry.bootstrap(TestBootstrappedClass.class, service);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail("Bootstrap method threw an exception!");
        }

        if (!service.hasRun()) {
            fail("No bootstrapped methods were executed!");
        }

        WinryContext.getContextFor(service).ifPresentOrElse(context -> {
            if (!((TestBootstrapper) context.getBootstrapper()).hasRun()) {
                fail("Bootstrapper didn't run.");
            }
        }, () -> fail("Cannot get WinryContext from provided service object!"));
    }
}
