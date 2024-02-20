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
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.network.aion.serverpackets.S_MATCHMAKER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.hamcrest.Matchers;

import java.util.List;

import static ch.lambdaj.Lambda.*;

public class AutoPvPFFAInstance extends AutoInstance
{
	private int worldId = 300350000;

	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		super.writeLock();
		try {
			if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize())) {
				return AGQuestion.FAILED;
			}
			players.put(player.getObjectId(), new AGPlayer(player));
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		finally {
			super.writeUnlock();
		}
	}

    @Override
    public void onEnterInstance(Player player) {
        super.onEnterInstance(player);
		long useDelay = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId());
        List<Player> playersByRace = instance.getPlayersInside();
		if (!playersByRace.isEmpty()) {
			if (player.getPortalCooldownList().getPortalCooldownItem(worldId) == null) {
				player.getPortalCooldownList().addPortalCooldown(worldId, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
			} else {
				player.getPortalCooldownList().addEntry(worldId);
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
		if (agt.isPvPFFAArena()) {
			if (!decrease(player, 186000135, 1)) { //Arena Ticket.
			    players.remove(player.getObjectId());
			    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 5));
			    if (players.isEmpty()) {
				    AutoGroupService.getInstance().unRegisterInstance(instance.getInstanceId());
			    }
			    return;
			}
		} else if (agt.isPvPSoloArena()) {
			if (!decrease(player, 186000136, 1)) { //Arena Of Discipline Ticket.
			    players.remove(player.getObjectId());
			    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 5));
			    if (players.isEmpty()) {
				    AutoGroupService.getInstance().unRegisterInstance(instance.getInstanceId());
			    }
			    return;
			}
		} else if (agt.isGloryArena()) {
			if (!decrease(player, 186020114, 1)) { //Arena Of Glory Ticket.
			    players.remove(player.getObjectId());
			    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 5));
			    if (players.isEmpty()) {
				    AutoGroupService.getInstance().unRegisterInstance(instance.getInstanceId());
			    }
			    return;
			}
		}
		((PvPArenaReward) instance.getInstanceHandler().getInstanceReward()).portToPosition(player);
		instance.register(player.getObjectId());
	}

	@Override
	public void onLeaveInstance(Player player) {
		super.unregister(player);
	}

	private List<AGPlayer> getPlayersByClass(PlayerClass playerClass) {
        return select(players, having(on(AGPlayer.class).getPlayerClass(), Matchers.equalTo(playerClass)));
    }
}
