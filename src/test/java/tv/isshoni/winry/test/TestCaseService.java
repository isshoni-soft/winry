package tv.isshoni.winry.test;

import junit.framework.TestCase;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.entity.inject.InjectedType;

@Injected(InjectedType.SERVICE)
public class TestCaseService {

    public void fail(String message) {
        TestCase.fail(message);
    }
}
