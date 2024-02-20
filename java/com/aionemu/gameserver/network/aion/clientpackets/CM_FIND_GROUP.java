package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MATCH;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_FIND_GROUP extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_FIND_GROUP.class);
	private int action;
    private int playerObjId;
    private String message;
    private int groupType;
    private int classId;
    private int level;
    private int unk;
    private int instanceId;
    private int minMembers;
    private int groupId;
	
	public CM_FIND_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		action = readC();
		switch (action) {
            case 0:
            break;
            case 1:
                playerObjId = readD();
                readH();
                readC();
                readC();
            break;
            case 2:
                playerObjId = readD();
                message = readS();
                groupType = readC();
            break;
            case 3:
                playerObjId = readD();
                readH();
                readC();
                readC();
                message = readS();
                groupType = readC();
            break;
            case 4:
            break;
            case 5:
                playerObjId = readD();
            break;
            case 6:
                playerObjId = readD();
                message = readS();
                groupType = readC();
                classId = readC();
                level = readC();
            break;
            case 7:
                playerObjId = readD();
                message = readS();
                groupType = readC();
                classId = readC();
                level = readC();
            break;
            case 8:
                instanceId = readD();
                groupType = readC();
                message = readS();
                minMembers = readC();
                unk = readD();
                unk = readD();
            break;
            case 9:
                unk = readD();
                instanceId = readD();
            break;
            case 20:
                groupId = readD();
                instanceId = readD();
                unk = readC();
            break;
            case 10:
            break;
            case 13:
            default:
				//log.error("Unknown find group packet? 0x" + Integer.toHexString(action).toUpperCase());
			break;
        }
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        } switch (this.action) {
			case 0:
			case 4:
				FindGroupService.getInstance().sendFindGroups(player, this.action);
			break;
			case 1:
			case 5:
				FindGroupService.getInstance().removeFindGroup(player.getRace(), this.action - 1, this.playerObjId);
			break;
			case 2:
			case 6:
				FindGroupService.getInstance().addFindGroupList(player, this.action, this.message, this.groupType);
			break;
			case 3:
			case 7:
				FindGroupService.getInstance().updateFindGroupList(player, this.message, this.playerObjId);
			break;
			case 8:
				FindGroupService.getInstance().registerInstanceGroup(player, 14, this.instanceId, this.message, this.minMembers, this.groupType);
			break;
			case 20:
			break;
			case 10:
				FindGroupService.getInstance().sendFindGroups(player, this.action);
			break;
			case 13:
				FindGroupService.getInstance().sendFindGroups(player, this.action);
			break;
			default:
				PacketSendUtility.sendPacket(player, new S_PARTY_MATCH(this.action, this.playerObjId, this.unk));
			break;
		}
	}
}