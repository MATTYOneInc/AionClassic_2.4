package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.event.EventsWindowTemplate;

import java.sql.Timestamp;

public class PlayerEventWindow {

    private EventsWindowTemplate template;
    private int id;
    private int elapsed;
    private int receivedCount;
    private Timestamp lastReceived;

    public PlayerEventWindow(int id, int elapsed, int receivedCount) {
        this.id = id;
        this.elapsed = elapsed;
        this.receivedCount = receivedCount;
        this.template = DataManager.EVENTS_WINDOW.getEventWindowId(this.id);
    }

    public int getId() {
        return id;
    }

    public EventsWindowTemplate getTemplate() {
        return template;
    }

    public int getElapsed() {
        return elapsed;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }

    public void addCount() {
        this.receivedCount +=1;
    }
}
