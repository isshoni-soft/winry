package tv.isshoni.winry.test;

import junit.framework.TestCase;
import tv.isshoni.winry.annotation.Injected;

@Injected
public class TestCaseService {

    public void fail(String message) {
        TestCase.fail(message);
    }
}
