package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownList;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.templates.InstanceCooltime;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import javolution.util.FastMap;

public class S_INSTANCE_DUNGEON_COOLTIMES extends AionServerPacket
{
	private Player player;
	private boolean isAnswer;
	private int cooldownId;
	private int worldId;
	private TemporaryPlayerTeam<?> playerTeam;
	
	public S_INSTANCE_DUNGEON_COOLTIMES(Player player, boolean isAnswer, TemporaryPlayerTeam<?> playerTeam) {
		this.player = player;
		this.isAnswer = isAnswer;
		this.playerTeam = playerTeam;
		this.worldId = 0;
		this.cooldownId = 0;
	}
	
	public S_INSTANCE_DUNGEON_COOLTIMES(Player player, int instanceId) {
		this.player = player;
		this.isAnswer = false;
		this.playerTeam = null;
		this.worldId = instanceId;
		this.cooldownId = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(instanceId) != null ? DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(instanceId).getId() : 0;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		boolean hasTeam = playerTeam != null;
		writeC(!isAnswer ? 0x2 : hasTeam ? 0x1 : 0x0);
		writeC(cooldownId);
		writeD(0x0); //unk1
		writeC(1); //unk
		if (cooldownId == 0) {

			writeH(hasTeam ? playerTeam.getMembers().size() : 1);
			if(hasTeam) {
				for (Player p : playerTeam.getMembers()) {
					writeD(p.getObjectId());
					writeH(DataManager.INSTANCE_COOLTIME_DATA.size());
					for (FastMap.Entry<Integer, InstanceCooltime> e = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().head(), end = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().tail(); (e = e.getNext()) != end;) {
						PortalCooldownList cooldownList = p.getPortalCooldownList();
						writeD(e.getValue().getId());
						writeD(0);
						writeD((int) DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(p, worldId));
						writeD(cooldownList.getPortalCooldownItem(e.getValue().getWorldId()) != null ? cooldownList.getPortalCooldownItem(e.getValue().getWorldId()).getEntryCount() : 0);
					}
					writeS(p.getName());
				}
			} else {
				PortalCooldownList cooldownList = player.getPortalCooldownList();
				writeD(player.getObjectId());
				writeH(DataManager.INSTANCE_COOLTIME_DATA.size());

				for (FastMap.Entry<Integer, InstanceCooltime> e = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().head(), end = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().tail(); (e = e.getNext()) != end;) {
					writeD(e.getValue().getId());
					writeD(0);
					writeD((int) DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
					writeD(cooldownList.getPortalCooldownItem(e.getValue().getWorldId()) != null ? cooldownList.getPortalCooldownItem(e.getValue().getWorldId()).getEntryCount() : 0);
				}
				writeS(player.getName());
			}
			writeS(player.getName());

		} else {
			writeH(1);
			writeD(player.getObjectId());
			writeH(1);
			writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId).getId());
			writeD(0);
			writeD((int) DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
			writeD(player.getPortalCooldownList().getPortalCooldownItem(worldId) != null ? player.getPortalCooldownList().getPortalCooldownItem(worldId).getEntryCount() : 0);
			writeS(player.getName());
		}

		/*
		if (isAnswer) {
			if (hasTeam) {	//all cooldowns from team
				writeH(playerTeam.getMembers().size());

				for (Player p : playerTeam.getMembers()) {
					PortalCooldownList cooldownList = p.getPortalCooldownList();
					writeD(p.getObjectId());
					writeH(cooldownList.size());

					for (FastMap.Entry<Integer, Long> e = cooldownList.getPortalCoolDowns().head(), end = cooldownList.getPortalCoolDowns().tail(); (e = e.getNext()) != end;) {
						writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(e.getKey()).getId());
						writeD(0x0);
						writeD(0);
						//writeD((int) (player.getPortalCooldownList().getPortalCooldown(worldId) - System.currentTimeMillis()) / 1000);
						writeD(-1); //2.4 entry count - used entry
					}
					writeS(p.getName());
				}
			}
			else {	//current cooldowns of player
				writeH(1);
				PortalCooldownList cooldownList = player.getPortalCooldownList();
				writeD(player.getObjectId());
				writeH(cooldownList.size());

				for (FastMap.Entry<Integer, Long> e = cooldownList.getPortalCoolDowns().head(), end = cooldownList.getPortalCoolDowns().tail(); (e = e.getNext()) != end;) {
					writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(e.getKey()).getId());
					writeD(0x0);
					writeD(0);
					//writeD((int) (player.getPortalCooldownList().getPortalCooldown(worldId) - System.currentTimeMillis()) / 1000);
					writeD(-1); //2.4 entry count - used entry
				}
				writeS(player.getName());
			}
		}
		else {
			if (cooldownId == 0) {	//all current cooldowns from player
				writeH(1);
				PortalCooldownList cooldownList = player.getPortalCooldownList();
				writeD(player.getObjectId());
				writeH(DataManager.INSTANCE_COOLTIME_DATA.size());
				for (FastMap.Entry<Integer, InstanceCooltime> e = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().head(), end = DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().tail(); (e = e.getNext()) != end;) {
					writeD(e.getValue().getId());
					writeD(0x0);
					writeD(0);
					//writeD((int) (player.getPortalCooldownList().getPortalCooldown(worldId) - System.currentTimeMillis()) / 1000);
					writeD(1); //2.4 entry count - used entry
				}
				writeS(player.getName());
			}
			else {	//just new cooldown from instance enter
				writeH(1);
				writeD(player.getObjectId());
				writeH(1);

				writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId).getId());
				writeD(0x0);
				writeD(0);
				//writeD((int) (player.getPortalCooldownList().getPortalCooldown(worldId) - System.currentTimeMillis()) / 1000);
				writeD(1); //2.4 entry count - used entry
				writeS(player.getName());
			}
		}*/
	}
}