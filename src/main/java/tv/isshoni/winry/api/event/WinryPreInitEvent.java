package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.annotation.Event;

@Event(value = "winry-pre-init", executable = true, weight = 500000)
public class WinryPreInitEvent { }
