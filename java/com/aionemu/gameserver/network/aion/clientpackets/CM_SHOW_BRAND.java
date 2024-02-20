package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_TACTICS_SIGN;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_SHOW_BRAND extends AionClientPacket
{
    @SuppressWarnings("unused")
    private int action;
    private int brandId;
    private int targetObjectId;
	
    public CM_SHOW_BRAND(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        action = readD();
        brandId = readD();
        targetObjectId = readD();
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if (player.isInGroup2()) {
            if (player.getPlayerGroup2().isLeader(player)) {
                PlayerGroupService.showBrand(player, targetObjectId, brandId);
            }
        } else if (player.isInAlliance2()) {
            if (player.getPlayerAlliance2().isSomeCaptain(player)) {
                PlayerAllianceService.showBrand(player, targetObjectId, brandId);
            }
        } else {
            PacketSendUtility.sendPacket(player, new S_TACTICS_SIGN(brandId, targetObjectId));
        }
    }
}