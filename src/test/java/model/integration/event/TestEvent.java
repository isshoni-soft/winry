package model.integration.event;

import tv.isshoni.winry.api.event.WinryEvent;

public class TestEvent extends WinryEvent {

    private int data;

    public TestEvent(int data) {
        super("TestEvent", false);
        this.data = data;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
