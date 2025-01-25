package institute.isshoni.winry.api.event;

import institute.isshoni.winry.api.annotation.Event;

@Event(value = "winry-shutdown", executable = true, weight = Integer.MIN_VALUE)
public class WinryShutdownEvent { }
