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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("grave_slime")
public class Grave_SlimeAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleDied() {
		announceGraveSlime();
		spawn(281671, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
		spawn(281671, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
	
	private void announceGraveSlime() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Grave Slime is splitting in two!
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDCatacombs_Normal_Slime_Isolation, 0);
				}
			}
		});
	}
}