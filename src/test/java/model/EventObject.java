package model;

import institute.isshoni.araragi.data.Constant;
import model.integration.event.TestEvent;
import institute.isshoni.winry.api.annotation.Listener;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.event.IListener;

public class EventObject implements IListener {

    private int receivedEvent;

    private final Constant<IWinryContext> context;

    public EventObject(@Context IWinryContext context) {
        this.receivedEvent = 0;
        this.context = new Constant<>(context);
    }

    @Listener(TestEvent.class)
    public void onTestEvent() {
        this.receivedEvent++;
    }

    public int hasReceivedEvent() {
        return this.receivedEvent;
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
