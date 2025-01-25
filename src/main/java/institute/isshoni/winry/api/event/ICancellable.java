package institute.isshoni.winry.api.event;

public interface ICancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
