package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerWardrobeService;

public class CM_USER_CLASSIC_WARDROBE_REGISTER extends AionClientPacket {

    private int objectId;

    public CM_USER_CLASSIC_WARDROBE_REGISTER(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.objectId = readD();
    }

    @Override
    protected void runImpl() {
        PlayerWardrobeService.getInstance().onRegisterItem(getConnection().getActivePlayer(), this.objectId);
    }
}