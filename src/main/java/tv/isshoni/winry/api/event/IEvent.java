package tv.isshoni.winry.api.event;

import tv.isshoni.winry.api.context.IContextual;

public interface IEvent extends IContextual {

    boolean isAsync();

    String getName();
}
