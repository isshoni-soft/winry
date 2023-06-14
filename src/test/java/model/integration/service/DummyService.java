package model.integration.service;

import tv.isshoni.winry.api.annotation.Injected;

@Injected
public class DummyService {

    private int INT = 0;

    public int get() {
        return INT++;
    }
}
