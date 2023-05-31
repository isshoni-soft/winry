package integration.test;

import model.integration.TestBootstrapper;
import model.integration.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tv.isshoni.winry.api.Winry;
import tv.isshoni.winry.internal.WinryContext;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestIntegration {

    @Parameterized.Parameters
    public static Object[][] testClasses() {
        return new Object[][] {
                { TestObjectFactory.class },
                { TestBackload.class },
                { TestOnMain.class },
                { TestAsync.class }
        };
    }

    private final Class<?> bootstrapped;

    public TestIntegration(Class<?> bootstrapped) {
        this.bootstrapped = bootstrapped;
    }

    @Test
    public void testBootstrapper() {
        TestService service = new TestService();

        try {
            Winry.bootstrap(this.bootstrapped, service);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail("Bootstrap method threw an exception!");
        }

        if (!service.hasRun()) {
            fail("No bootstrapped methods were executed!");
        }

        if (service.getFailureMessage() != null) {
            fail(service.getFailureMessage());
        }

        WinryContext.getContextFor(service).ifPresentOrElse(context -> {
            if (!((TestBootstrapper) context.getBootstrapper()).hasRun()) {
                fail("Bootstrapper didn't run.");
            }
        }, () -> fail("Cannot get WinryContext from provided service object!"));
    }
}
