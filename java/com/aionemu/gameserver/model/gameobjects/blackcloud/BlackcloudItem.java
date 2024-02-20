package com.aionemu.gameserver.model.gameobjects.blackcloud;

public class BlackcloudItem {

    private final int blackCloudId;
    private final int itemId;
    private final int itemCount;

    public BlackcloudItem(int blackCloudId, int itemId, int itemCount) {
        this.blackCloudId = blackCloudId;
        this.itemId = itemId;
        this.itemCount = itemCount;
    }


    public int getBlackCloudId() {
        return blackCloudId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemCount() {
        return itemCount;
    }
}
