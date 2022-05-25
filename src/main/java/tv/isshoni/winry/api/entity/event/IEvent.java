package tv.isshoni.winry.api.entity.event;

import tv.isshoni.winry.api.entity.context.IContextual;

import java.util.List;

public interface IEvent extends IContextual {

    boolean isAsync();

    String getName();
}
