package tv.isshoni.winry.test.model;

import tv.isshoni.winry.annotation.Async;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.test.model.processor.Profile;

@Injected
public class TestInjectedClass {

    private int numCalled = 0;

    public int getNumCalled() {
        return this.numCalled++;
    }

    @Profile
    public void testProfiling() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async
    public int getTest() {
        return 5;
    }
}
