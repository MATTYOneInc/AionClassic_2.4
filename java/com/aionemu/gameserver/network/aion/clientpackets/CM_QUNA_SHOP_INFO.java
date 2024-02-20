package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_SHOP_CATEGORY_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_QUNA_SHOP_INFO extends AionClientPacket{

    public CM_QUNA_SHOP_INFO(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }


    @Override
    protected void readImpl() {
        readC();
        readS();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PacketSendUtility.sendPacket(player, new S_SHOP_CATEGORY_INFO(0, 0));
    }
}
