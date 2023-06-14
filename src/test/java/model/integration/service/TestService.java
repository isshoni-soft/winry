package model.integration.service;

import tv.isshoni.winry.api.annotation.Injected;

@Injected
public class TestService {

    private String failureMessage;

    private boolean hasRun;

    public void run() {
        this.hasRun = true;
    }

    public void fail(String message) {
        this.failureMessage = message;
    }

    public boolean hasRun() {
        return this.hasRun;
    }

    public String getFailureMessage() {
        return this.failureMessage;
    }
}
