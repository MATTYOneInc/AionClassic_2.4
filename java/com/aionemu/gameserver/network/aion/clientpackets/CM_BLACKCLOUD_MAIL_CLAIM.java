package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.BlackCloudTradeService;

public class CM_BLACKCLOUD_MAIL_CLAIM extends AionClientPacket {

    private int objectId;

    public CM_BLACKCLOUD_MAIL_CLAIM(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }


    @Override
    protected void readImpl() {
        this.objectId = readD();
        readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        BlackCloudTradeService.getInstance().onClaimItem(player, this.objectId);
    }
}
