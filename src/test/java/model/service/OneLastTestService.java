package model.service;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import model.integration.model.TestInjectedClass;

import static org.junit.Assert.assertNotNull;

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
