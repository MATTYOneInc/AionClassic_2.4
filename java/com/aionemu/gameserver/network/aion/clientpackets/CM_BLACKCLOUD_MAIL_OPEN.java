package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPSHOP_GOODS_COUNT;
import com.aionemu.gameserver.network.aion.serverpackets.need.S_RESPONSE_NPSHOP_GOODS_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_BLACKCLOUD_MAIL_OPEN extends AionClientPacket {

    public CM_BLACKCLOUD_MAIL_OPEN(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PacketSendUtility.sendPacket(player, new S_NPSHOP_GOODS_COUNT(player.getBlackcloudLetters().size()));
        PacketSendUtility.sendPacket(player, new S_RESPONSE_NPSHOP_GOODS_LIST(player));
    }
}