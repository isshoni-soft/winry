package tv.isshoni.winry.test;

import tv.isshoni.winry.api.annotation.Injected;

import junit.framework.TestCase;

@Injected
public class TestCaseService {

    private boolean hasRun;

    public void run() {
        this.hasRun = true;
    }

    public boolean hasRun() {
        return this.hasRun;
    }

    public void fail(String message) {
        TestCase.fail(message);
    }
}
