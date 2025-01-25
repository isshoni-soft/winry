package institute.isshoni.winry.api.event;

import institute.isshoni.winry.api.annotation.Event;

@Event(value = "winry-post-init", executable = true, weight = 50000)
public class WinryPostInitEvent { }
