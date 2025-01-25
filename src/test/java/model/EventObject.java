package model;

import institute.isshoni.araragi.data.Constant;
import model.integration.event.TestEvent;
import tv.isshoni.winry.api.annotation.Listener;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.event.IListener;

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
