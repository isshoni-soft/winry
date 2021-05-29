package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.annotation.Injected;

@Injected
public class TestInjectedClass {

    private int numCalled = 0;

    public int getNumCalled() {
        return this.numCalled++;
    }

    @Async
    public int getTest() {
        return 5;
    }
}
