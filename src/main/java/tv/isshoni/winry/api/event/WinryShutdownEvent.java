package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.annotation.Event;

@Event(value = "winry-shutdown", executable = true, weight = Integer.MIN_VALUE)
public class WinryShutdownEvent { }
