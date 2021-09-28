package tv.isshoni.winry.entity.event;

public interface ICancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
