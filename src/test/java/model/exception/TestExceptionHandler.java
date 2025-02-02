package model.exception;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.winry.api.annotation.exception.Handler;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.exception.IExceptionHandler;

@Handler(value = TestException.class, enforceSingleton = true)
public class TestExceptionHandler implements IExceptionHandler<TestException> {

    private final AraragiLogger logger;

    private boolean hasRun = false;

    public TestExceptionHandler(@Context IWinryContext context) {
        this.logger = context.getLoggerFactory().createLogger("TestExceptionHandler");
    }

    @Override
    public void handle(TestException exception) {
        this.logger.info("Successfully handled TestException!");
        this.hasRun = true;
    }

    public boolean hasRun() {
        return this.hasRun;
    }
}
