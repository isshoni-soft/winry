package tv.isshoni.winry.entity.event;

import java.util.List;

public interface IEvent {

    boolean isAsync();

    String getName();

    List<IEventHandler> getHandlers();
}
