package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.annotation.Event;

@Event(value = "winry-post-init", executable = true, weight = 50000)
public class WinryPostInitEvent { }
