package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.BlackCloudTradeService;

public class CM_BLACKCLOUD_MAIL extends AionClientPacket {

    public CM_BLACKCLOUD_MAIL(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }


    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        BlackCloudTradeService.getInstance().onOpenBlackCloudBox(player);
    }
}
