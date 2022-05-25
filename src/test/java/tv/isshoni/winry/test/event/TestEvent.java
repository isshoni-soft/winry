package tv.isshoni.winry.test.event;

import tv.isshoni.winry.api.entity.event.WinryEvent;

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
