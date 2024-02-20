package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.model.gameobjects.player.WardrobeEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_USER_CLASSIC_WARDROBE_DATA_UPDATED extends AionServerPacket {

    private final int action;
    private final WardrobeEntry wardrobeEntry;

    public S_USER_CLASSIC_WARDROBE_DATA_UPDATED(int action, WardrobeEntry wardrobeEntry) {
        this.action = action;
        this.wardrobeEntry = wardrobeEntry;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(this.action);
        writeD(this.wardrobeEntry.getObjectId());
        writeD(this.wardrobeEntry.getItemId());
        writeD(this.wardrobeEntry.getColor());
        writeC(this.wardrobeEntry.isLiked() ? 1 : 0);
    }
}