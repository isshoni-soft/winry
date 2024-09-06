package model.integration.service;

import tv.isshoni.winry.api.annotation.Inject;
import tv.isshoni.winry.api.annotation.Injected;

@Injected
public class NestedService {

    @Inject private DummyService dummyService;

    public DummyService getDummyService() {
        return this.dummyService;
    }
}
