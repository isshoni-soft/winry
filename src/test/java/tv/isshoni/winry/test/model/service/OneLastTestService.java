package tv.isshoni.winry.test.model.service;

import tv.isshoni.winry.annotation.Injected;

@Injected
public class OneLastTestService {

    private String test;

    public OneLastTestService() {
        this.test = "test";
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return this.test;
    }
}
