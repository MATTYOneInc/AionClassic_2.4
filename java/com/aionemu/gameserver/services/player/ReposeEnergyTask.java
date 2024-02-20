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
package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_EXP;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReposeEnergyTask implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(ReposeEnergyTask.class);
    private final int playerId;
	
    ReposeEnergyTask(int playerId) {
        this.playerId = playerId;
    }
	
    public void run() {
        Player player = World.getInstance().findPlayer(playerId);
		PlayerCommonData pcd = player.getCommonData();
        if (player != null && player.isInState(CreatureState.RESTING) && !player.isInInstance() && pcd.getCurrentReposteEnergy() < pcd.getMaxReposteEnergy()) {
			pcd.addReposteEnergy(3388542); //5%
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
			PacketSendUtility.sendPacket(player, new S_EXP(pcd.getExpShown(), pcd.getExpRecoverable(), pcd.getExpNeed(), pcd.getCurrentReposteEnergy(), pcd.getMaxReposteEnergy()));
        }
    }
}