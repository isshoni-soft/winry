package tv.isshoni.winry.test.model.service;

import static org.junit.Assert.assertNotNull;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.winry.annotation.Inject;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.annotation.parameter.Context;
import tv.isshoni.winry.entity.context.IWinryContext;
import tv.isshoni.winry.test.model.TestInjectedClass;

@Injected
public class OneLastTestService {

    @Logger private IAraragiLogger LOGGER;

    @Inject private TestInjectedClass injectedClass;

    private String test;

    public OneLastTestService(@Context IWinryContext context) {
        this.test = "test";

        assertNotNull(context);
    }

    public int getInjectedClassVal() {
        return this.injectedClass.getNumCalled();
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return this.test;
    }
}
