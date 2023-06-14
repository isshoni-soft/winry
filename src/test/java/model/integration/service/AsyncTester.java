package model.integration.service;

import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.transformer.Async;
import tv.isshoni.winry.api.annotation.transformer.OnMain;

@Injected
public class AsyncTester {

    @Async
    public void asyncMethod(long callerThread, TestService testService) {
        if (Thread.currentThread().getId() == callerThread) {
            testService.fail("running thread: " + Thread.currentThread().getId() + " is caller thread: " + callerThread);
        }
    }

    @OnMain
    public void onMainMethod(TestService testService) {
        if (Thread.currentThread().getId() != 1) {
            testService.fail("Current thread id is " + Thread.currentThread().getId() + " it should be 1!");
        }
    }
}
