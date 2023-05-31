package model.service;

import model.integration.TestService;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.transformer.Async;

@Injected
public class AsyncTester {

    @Async
    public void asyncMethod(long callerThread, TestService testService) {
        if (Thread.currentThread().getId() == callerThread) {
            testService.fail("running thread: " + Thread.currentThread().getId() + " is caller thread: " + callerThread);
        }
    }
}
