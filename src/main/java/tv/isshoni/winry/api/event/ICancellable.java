package tv.isshoni.winry.api.event;

public interface ICancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
