package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_TARGET_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_USER_CHANGED_TARGET;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_TARGET_SELECT extends AionClientPacket
{
	private int targetObjectId;
	private int type;
	
	public CM_TARGET_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		type = readC();
	}
	
	protected void runImpl() {
        VisibleObject obj;
        Player player = getConnection().getActivePlayer();
		if (player == null) {
            return;
        } if (targetObjectId == player.getObjectId()) {
            obj = player;
        } else {
            obj = player.getKnownList().getObject(targetObjectId);
            if (obj == null && player.isInTeam()) {
				TeamMember<Player> member = player.getCurrentTeam().getMember(targetObjectId);
				if (member != null) {
					obj = member.getObject();
				}
			}
        } if (obj != null) {
            if (type == 1) {
                if (obj.getTarget() == null) {
                    return;
                }
                player.setTarget(obj.getTarget());
            } else {
                player.setTarget(obj);
            }
        } else {
            player.setTarget(null);
        }
        sendPacket(new S_TARGET_INFO(player));
        PacketSendUtility.broadcastPacket(player, new S_USER_CHANGED_TARGET(player));
    }
}