package tv.isshoni.winry.api.event;

public class WinryShutdownEvent extends WinryEvent {

    public WinryShutdownEvent() {
        super("Shutdown", false);
    }
}
