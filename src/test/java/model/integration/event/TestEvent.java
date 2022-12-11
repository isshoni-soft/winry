package model.integration.event;

import tv.isshoni.winry.api.annotation.Event;

@Event("TestEvent")
public class TestEvent {

    private int data;

    public TestEvent(int data) {
        this.data = data;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
