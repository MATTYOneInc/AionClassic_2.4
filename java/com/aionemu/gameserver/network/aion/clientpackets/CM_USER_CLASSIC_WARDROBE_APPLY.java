package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerWardrobeService;

public class CM_USER_CLASSIC_WARDROBE_APPLY extends AionClientPacket {

    private int itemObj;
    private int objectId;

    public CM_USER_CLASSIC_WARDROBE_APPLY(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.itemObj = readD();
        this.objectId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PlayerWardrobeService.getInstance().onReskin(player, this.objectId, this.itemObj);
    }
}