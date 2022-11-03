package tv.isshoni.winry.api.entity.event;

public interface ICancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
