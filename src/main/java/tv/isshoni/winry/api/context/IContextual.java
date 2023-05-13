package tv.isshoni.winry.api.context;

import tv.isshoni.araragi.data.Constant;

public interface IContextual {

    Constant<IWinryContext> getContext();
}
