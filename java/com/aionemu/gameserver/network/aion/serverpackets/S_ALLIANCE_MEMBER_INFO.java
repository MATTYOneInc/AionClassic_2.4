/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.world.WorldPosition;

import java.util.List;

public class S_ALLIANCE_MEMBER_INFO extends AionServerPacket
{
	private Player player;
	private PlayerAllianceEvent event;
	private final int allianceId;
	private final int objectId;

	public S_ALLIANCE_MEMBER_INFO(PlayerAllianceMember member, PlayerAllianceEvent event) {
		this.player = member.getObject();
		this.event = event;
		this.allianceId = member.getAllianceId();
		this.objectId = member.getObjectId();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();
		if (event == PlayerAllianceEvent.ENTER && !player.isOnline()) {
			event = PlayerAllianceEvent.ENTER_OFFLINE;
		}
		writeD(allianceId);
		writeD(objectId);
		if (player.isOnline()) {
			PlayerLifeStats pls = player.getLifeStats();
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp());
			writeD(pls.getCurrentFp());
		} else {
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}

		writeD(0); // unknown int
		writeD(wp.getMapId());
		writeD(wp.getMapId());
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId());
		writeC(pcd.getGender().getGenderId());
		writeC(pcd.getLevel());
		writeC(event.getId());
		writeH(0); // player.isOnline() ? 1 : 0);
		writeC(0); // writeC(player.isMentor() ? 1 : 0);
		switch (event) {
			case LEAVE:
			case LEAVE_TIMEOUT:
			case BANNED:
			case MOVEMENT:
			case DISCONNECTED:
				break;
			case ENTER:
			case UPDATE:
			case RECONNECT:
			case ENTER_OFFLINE:
			case DEMOTE_VICE_CAPTAIN:
			case APPOINT_VICE_CAPTAIN:
				writeS(pcd.getName());
				writeD(0x00);
				writeD(0x00);
				if (player.isOnline()) {
					List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
					writeH(abnormalEffects.size());
					for (Effect effect : abnormalEffects) {
						writeD(effect.getEffectorId());
						writeH(effect.getSkillId());
						writeC(effect.getSkillLevel());
						writeC(effect.getTargetSlot());
						writeD(effect.getRemainingTime());
					}
				} else {
					writeH(0x00);
				}
				break;
			case JOIN:
			case MEMBER_GROUP_CHANGE:
				writeS(pcd.getName());
				break;
			default:
				break;
		}
	}
}
