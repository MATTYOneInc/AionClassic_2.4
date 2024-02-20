package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.world.WorldPosition;

import java.util.List;

public class S_PARTY_MEMBER_INFO extends AionServerPacket
{
	private int groupId;
	private Player player;
	private GroupEvent event;

	public S_PARTY_MEMBER_INFO(PlayerGroup group, Player player, GroupEvent event) {
		this.groupId = group.getTeamId();
		this.player = player;
		this.event = event;
	}

	@Override
	protected void writeImpl(AionConnection con)
	{
		PlayerLifeStats pls = player.getLifeStats();
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();
		if (wp == null) {
			return;
		}
		if (event == GroupEvent.ENTER && !player.isOnline()) {
			event = GroupEvent.ENTER_OFFLINE;
		}
		if (event == GroupEvent.UPDATE && !player.isOnline()) {
			event = GroupEvent.DISCONNECTED;
		}
		writeD(groupId);
		writeD(player.getObjectId());
		if (player.isOnline()) {
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

		writeD(0);//unk 2.4
		writeD(wp.getMapId());
		writeD(wp.getMapId());
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId());
		writeC(pcd.getGender().getGenderId());
		writeC(pcd.getLevel());

		writeC(event.getId());
		writeH(player.isOnline() ? 1 : 0);
		writeC(player.isMentor() ? 1 : 0);

		switch (event) {
			case MOVEMENT:
			case DISCONNECTED:
			break;
			case LEAVE:
				writeH(0x00);
				writeC(0x00);
			break;
			case JOIN:
			case ENTER_OFFLINE:
				writeS(pcd.getName());
			break;
			case ENTER:
			case UPDATE:
				writeS(pcd.getName());
				writeD(0x00);
				writeD(0x00);
				List<Effect> abnormalEffects1 = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects1.size());
				for (Effect effect : abnormalEffects1) {
					writeD(effect.getEffectorId());
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
				}
				writeD(2835); // packet on this condition need to be ended with 4 bytes (int)
				//writeB("73E60100");//dont touch
			break;
		}
	}
}
