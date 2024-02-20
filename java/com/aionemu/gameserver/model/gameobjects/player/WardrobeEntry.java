package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class WardrobeEntry {

    private final int objectId;
    private final int itemId;
    private int color;
    private boolean liked;

    public WardrobeEntry(int objectId, int itemId, int color, boolean liked) {
        this.objectId = objectId;
        this.itemId = itemId;
        this.color = color;
        this.liked = liked;
    }

    public int getItemId() {
        return itemId;
    }

    public int getObjectId() {
        return objectId;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public ItemTemplate getItemSkinTemplate() {
        return DataManager.ITEM_DATA.getItemTemplate(this.itemId);
    }
}
