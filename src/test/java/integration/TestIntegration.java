package integration;

import integration.test.TestEventExecution;
import model.integration.TestBootstrapper;
import model.integration.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;
import tv.isshoni.winry.api.Winry;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.internal.WinryContext;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestIntegration {

    // run specific bootstrapper for debugging
    public static void main(String... args) {
        new TestIntegration(TestEventExecution.class, "").testBootstrapper();
    }

    @Parameterized.Parameters(name = "{index} : {1}")
    public static Object[][] testClasses() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
                .forPackages("integration.test")
                .filterInputsBy(new FilterBuilder().includePackage("integration.test")));

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Bootstrap.class);

        Object[][] objects = new Object[classes.size()][2];

        int x = 0;
        for (Class<?> clazz : classes) {
            Bootstrap bootstrap = clazz.getAnnotation(Bootstrap.class);

            objects[x][0] = clazz;
            objects[x][1] = bootstrap.name();
            x++;
        }

        return objects;
    }

    private final Class<?> bootstrapped;

    public TestIntegration(Class<?> bootstrapped, String testName) {
        this.bootstrapped = bootstrapped;
    }

    @Test
    public void testBootstrapper() {
        TestService service = new TestService();

        try {
            Winry.bootstrap(this.bootstrapped, new Object[] { service });
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
            try {
                if (!((TestBootstrapper) context.getBootstrapper()).hasRun()) {
                    fail("Bootstrapper didn't run.");
                }
            } catch (ClassCastException e) {
                fail("Bootstrapper is not TestBootstrapper!");
            }
        }, () -> fail("Cannot get WinryContext from provided service object!"));
    }
}
