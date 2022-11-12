package unit.manager;

import model.exception.RuntimeExceptionHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.internal.annotation.manage.WinryAnnotationManager;
import tv.isshoni.winry.internal.exception.WinryExceptionManager;
import tv.isshoni.winry.internal.logging.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestWinryExceptionManager {

    private WinryExceptionManager exceptionManager;

    private static LoggerFactory loggerFactory;

    @Mock private WinryAnnotationManager annotationManager;

    @BeforeClass
    public static void beforeClass() {
        loggerFactory = mock(LoggerFactory.class);

        when(loggerFactory.createLogger(any(Class.class)))
                .thenReturn(AraragiLogger.create("ExceptionManager"));
    }

    @Before
    public void before() {
        this.exceptionManager = new WinryExceptionManager(loggerFactory, this.annotationManager);
    }

    @Test
    public void testRegister() throws Throwable {
        when(this.annotationManager.construct(RuntimeExceptionHandler.class))
                .thenReturn(new RuntimeExceptionHandler());

        this.exceptionManager.registerGlobal(RuntimeExceptionHandler.class);

        assertEquals(1, this.exceptionManager.getGlobalHandlers().size());

        this.exceptionManager.getGlobalHandlers().forEach((t, lh) -> {
            assertEquals(RuntimeException.class, t);

            assertEquals(1, lh.size());

            lh.forEach(Assert::assertNotNull);
        });
    }
}
