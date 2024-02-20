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
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("divineartifact")
public class Divine_ArtifactAI2 extends AggressiveNpcAI2
{
	private boolean cooldown = false;
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!cooldown) {
			announceDivineArtifact();
			AI2Actions.useSkill(this, 18915);
			setCD();
		}
	}
	
	private void setCD() {
		cooldown = true;
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				cooldown = false;
			}
		}, 1000);
	}
	
	private void announceDivineArtifact() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Divine Artifact has been activated!
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_IDCatacombs_Boss_ArchPriest_Artifact_Light);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}