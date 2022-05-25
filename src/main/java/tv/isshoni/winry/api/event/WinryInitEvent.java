package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.entity.event.WinryEvent;

public class WinryInitEvent extends WinryEvent {

    public WinryInitEvent() {
        super("Init", false);
    }
}
