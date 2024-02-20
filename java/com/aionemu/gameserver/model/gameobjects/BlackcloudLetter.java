package com.aionemu.gameserver.model.gameobjects;

import java.sql.Timestamp;

public class BlackcloudLetter extends AionObject{

    private Timestamp timeStamp;
    private LetterType letterType;
    private boolean unread;
    private PersistentState persistentState;
    private Item attachedItem;
    private String message;

    public BlackcloudLetter(int objId, Item attachedItem, String message, Timestamp timeStamp, boolean unread) {
        super(objId);
        this.attachedItem = attachedItem;
        this.message = message;
        this.timeStamp = timeStamp;
        this.unread = unread;
        this.letterType = LetterType.BLACKCLOUD;
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

    public Item getAttachedItem() {
        return attachedItem;
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

    @Override
    public String getName() {
        return "";
    }
}
