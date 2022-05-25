package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.entity.event.WinryEvent;

public class WinryShutdownEvent extends WinryEvent {

    public WinryShutdownEvent() {
        super("Shutdown", false);
    }
}
