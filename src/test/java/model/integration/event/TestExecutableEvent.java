package model.integration.event;

import tv.isshoni.winry.api.annotation.Event;

@Event(value = "test-executable-event", executable = true, weight = 500000)
public class TestExecutableEvent { }
