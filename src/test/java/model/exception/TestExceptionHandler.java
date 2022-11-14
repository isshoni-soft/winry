package model.exception;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.exception.IExceptionHandler;

@Handler(value = TestException.class, enforceSingleton = true)
public class TestExceptionHandler implements IExceptionHandler<TestException> {

    private final AraragiLogger logger;

    private boolean hasRun = false;

    public TestExceptionHandler(@Context IWinryContext context) {
        this.logger = context.getLoggerFactory().createLogger(this.getClass());
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
