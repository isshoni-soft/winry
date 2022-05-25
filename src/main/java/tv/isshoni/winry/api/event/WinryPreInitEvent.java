package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.entity.event.WinryEvent;

public class WinryPreInitEvent extends WinryEvent {

    public WinryPreInitEvent() {
        super("PreInit", false);
    }
}
