package com.aionemu.gameserver.model.gameobjects.blackcloud;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import javolution.util.FastList;

import java.sql.Timestamp;
import java.util.List;

public class BlackcloudLetter extends AionObject {

    private Timestamp timeStamp;
    private LetterType letterType;
    private boolean unread;
    private PersistentState persistentState;
    private List<BlackcloudItem> attachedItem;
    private String title;
    private String message;

    public BlackcloudLetter(int objId, String title, String message, Timestamp timeStamp, boolean unread) {
        super(objId);
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
        this.unread = unread;
        this.letterType = LetterType.BLACKCLOUD;
        this.attachedItem = new FastList<BlackcloudItem>();
    }

    @Override
    public Integer getObjectId() {
        return super.getObjectId();
    }

    public boolean isUnread() {
        return unread;
    }

    public LetterType getLetterType() {
        return letterType;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void delete() {
        this.persistentState = PersistentState.DELETED;
    }

    public void setPersistState(PersistentState state) {
        this.persistentState = state;
    }

    public PersistentState getPersistentState() {
        return persistentState;
    }

    public List<BlackcloudItem> getAttachedItem() {
        return attachedItem;
    }

    public void setAttachedItem(List<BlackcloudItem> attachedItem) {
        this.attachedItem = attachedItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getName() {
        return "";
    }
}
