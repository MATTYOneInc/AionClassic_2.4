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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.TiakReward;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.instance.TiakResearchBaseService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

public class AutoTiakResearchBaseInstance extends AutoInstance
{
	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		super.writeLock();
		try {
			if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize())) {
				return AGQuestion.FAILED;
			}
			EntryRequestType ert = searchInstance.getEntryRequestType();
			List<AGPlayer> playersByRace = getAGPlayersByRace(player.getRace());
			if (ert.isGroupEntry()) {
				if (searchInstance.getMembers().size() + playersByRace.size() > 3) {
					return AGQuestion.FAILED;
				} for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
					if (searchInstance.getMembers().contains(member.getObjectId())) {
						players.put(member.getObjectId(), new AGPlayer(player));
					}
				}
			} else {
				if (playersByRace.size() >= 3) {
					return AGQuestion.FAILED;
				}
				players.put(player.getObjectId(), new AGPlayer(player));
			}
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		} finally {
			super.writeUnlock();
		}
	}

	@Override
    public void onEnterInstance(Player player) {
        super.onEnterInstance(player);
        long useDelay = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId());
		List<Player> playersByRace = getPlayersByRace(player.getRace());
		playersByRace.remove(player);
        if (playersByRace.size() == 1 && !playersByRace.get(0).isInGroup2()) {
            PlayerGroup newGroup = PlayerGroupService.createGroup(playersByRace.get(0), player, TeamType.GROUP);
            int groupId = newGroup.getObjectId();
            if (!instance.isRegistered(groupId)) {
                instance.register(groupId);
            }
			if (player.getPortalCooldownList().getPortalCooldownItem(instance.getMapId()) == null) {
				player.getPortalCooldownList().addPortalCooldown(instance.getMapId(), 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId()));
			} else {
				player.getPortalCooldownList().addEntry(instance.getMapId());
				//You have successfully entered the area, consuming one of your permitted entries.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE);
			}
        } else if (!playersByRace.isEmpty() && playersByRace.get(0).isInGroup2()) {
            PlayerGroupService.addPlayer(playersByRace.get(0).getPlayerGroup2(), player);
			if (player.getPortalCooldownList().getPortalCooldownItem(instance.getMapId()) == null) {
				player.getPortalCooldownList().addPortalCooldown(instance.getMapId(), 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId()));
			} else {
				player.getPortalCooldownList().addEntry(instance.getMapId());
				//You have successfully entered the area, consuming one of your permitted entries.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE);
			}
        }
        Integer object = player.getObjectId();
        if (!instance.isRegistered(object)) {
            instance.register(object);
        }
    }

	@Override
	public void onPressEnter(Player player) {
		super.onPressEnter(player);
		TiakResearchBaseService.getInstance().addCoolDown(player);
		((TiakReward) instance.getInstanceHandler().getInstanceReward()).portToPosition(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		super.unregister(player);
		PlayerGroupService.removePlayer(player);
		TeleportService2.moveToBindLocation(player, true);
	}

	private List<AGPlayer> getAGPlayersByRace(Race race) {
		return select(players, having(on(AGPlayer.class).getRace(), equalTo(race)));
	}

	private List<Player> getPlayersByRace(Race race) {
		return select(instance.getPlayersInside(), having(on(Player.class).getRace(), equalTo(race)));
	}
}
