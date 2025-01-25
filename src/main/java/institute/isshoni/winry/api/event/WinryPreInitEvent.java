package institute.isshoni.winry.api.event;

import institute.isshoni.winry.api.annotation.Event;

@Event(value = "winry-pre-init", executable = true, weight = 500000)
public class WinryPreInitEvent { }
