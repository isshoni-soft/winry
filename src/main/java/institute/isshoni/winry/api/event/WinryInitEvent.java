package institute.isshoni.winry.api.event;

import institute.isshoni.winry.api.annotation.Event;

@Event(value = "winry-init", executable = true, weight = 90000)
public class WinryInitEvent { }
