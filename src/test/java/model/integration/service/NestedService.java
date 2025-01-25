package model.integration.service;

import institute.isshoni.winry.api.annotation.Inject;
import institute.isshoni.winry.api.annotation.Injected;

@Injected
public class NestedService {

    @Inject private DummyService dummyService;

    public DummyService getDummyService() {
        return this.dummyService;
    }
}
