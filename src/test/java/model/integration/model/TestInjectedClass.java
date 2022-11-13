package model.integration.model;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.transformer.Async;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.Logger;
import tv.isshoni.winry.api.annotation.transformer.Profile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

@Injected
public class TestInjectedClass {

    @Logger("TestInjectedClass") private static AraragiLogger LOGGER;

    private int numCalled = 0;

    private volatile int selectedNum;

    @Profile
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

    public int getSelectedNum() {
        return this.selectedNum;
    }

    @Async
    public Future<Integer> asyncFutureMethod() {
        this.selectedNum = ThreadLocalRandom.current().nextInt(0, 1000);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(this.selectedNum);
    }

    @Async
    @Profile
    public void asyncMethod() {
        UUID uuid = UUID.randomUUID();

        LOGGER.info("Start running async method: " + uuid);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info("End running async method: " + uuid);
    }

    public int getTest() {
        return 5;
    }
}