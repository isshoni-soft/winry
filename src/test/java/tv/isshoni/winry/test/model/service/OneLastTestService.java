package tv.isshoni.winry.test.model.service;

import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.test.model.TestInjectedClass;

@Injected
public class OneLastTestService {

    @Inject private TestInjectedClass injectedclass;

    private String test;

    public OneLastTestService() {
        this.test = "test";
    }

    public int getInjectedClassVal() {
        return this.injectedclass.getNumCalled();
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return this.test;
    }
}
