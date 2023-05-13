package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.annotation.Event;

@Event(value = "winry-init", executable = true, weight = 90000)
public class WinryInitEvent { }
