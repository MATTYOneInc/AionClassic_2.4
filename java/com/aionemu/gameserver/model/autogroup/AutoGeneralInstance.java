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
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.hamcrest.Matchers;

import java.util.*;

import static ch.lambdaj.Lambda.*;

public class AutoGeneralInstance extends AutoInstance
{
	private int worldId = instance.getMapId();
	
	@Override
    public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
        super.writeLock();
        try {
            if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize())) {
                return AGQuestion.FAILED;
            }
            players.put(player.getObjectId(), new AGPlayer(player));
            return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
        } finally {
            super.writeUnlock();
        }
    }
	
	/*
	@Override
    public void onEnterInstance(Player player) {
        super.onEnterInstance(player);
        List<Player> playersByRace = instance.getPlayersInside();
        if (playersByRace.size() == 1 && !playersByRace.get(0).isInGroup2()) {
            PlayerGroup newGroup = PlayerGroupService.createGroup(playersByRace.get(0), player, TeamType.GROUP);
            int groupId = newGroup.getObjectId();
            if (!instance.isRegistered(groupId)) {
                instance.register(groupId);
            } if (player.getPortalCooldownList().getPortalCooldownItem(worldId) == null) {
                player.getPortalCooldownList().addPortalCooldown(worldId, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
            } else {
                player.getPortalCooldownList().addEntry(worldId);
                //Entry successful. Entry count consumed.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE);
            }
        } else if (!playersByRace.isEmpty() && playersByRace.get(0).isInGroup2()) {
            PlayerGroupService.addPlayer(playersByRace.get(0).getPlayerGroup2(), player);
            if (player.getPortalCooldownList().getPortalCooldownItem(worldId) == null) {
                player.getPortalCooldownList().addPortalCooldown(worldId, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
            } else {
                player.getPortalCooldownList().addEntry(worldId);
                //Entry successful. Entry count consumed.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE);
            }
        }
        Integer object = player.getObjectId();
        if (!instance.isRegistered(object)) {
            instance.register(object);
        }
    }*/
	
	@Override
	public void onPressEnter(Player player) {
		super.onPressEnter(player);
		PortalPath portal = DataManager.PORTAL2_DATA.getPortalDialog(worldId, 10000, player.getRace());
		if (portal == null) {
			return;
		}
		PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portal.getLocId());
		if (loc == null) {
			return;
		}
		TeleportService2.teleportTo(player, worldId, instance.getInstanceId(), loc.getX(), loc.getY(), loc.getZ(), loc.getH());
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		super.unregister(player);
		PlayerGroupService.removePlayer(player);
	}
	
	private List<AGPlayer> getPlayersByClass(PlayerClass playerClass) {
        return select(players, having(on(AGPlayer.class).getPlayerClass(), Matchers.equalTo(playerClass)));
    }
}