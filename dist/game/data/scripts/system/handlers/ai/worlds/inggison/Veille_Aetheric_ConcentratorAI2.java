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
package ai.worlds.inggison;

import ai.ActionItemNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("veille_aetheric_concentrator")
public class Veille_Aetheric_ConcentratorAI2 extends ActionItemNpcAI2
{
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		announceVeilleIII();
		SkillEngine.getInstance().getSkill(getOwner(), 20124, 60, getOwner()).useNoAnimationSkill(); //Aether Concentrator Standby.
    }
	
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		    //Veille's Aetheric Concentrator I
			case 296907:
				if (player.getLevel() >= 50) {
					announceVeilleI();
					AI2Actions.targetCreature(Veille_Aetheric_ConcentratorAI2.this, getPosition().getWorldMapInstance().getNpc(258200)); //Enraged Veille.
				    AI2Actions.useSkill(Veille_Aetheric_ConcentratorAI2.this, 20107); //Defense Aether.
				} else {
					//You have failed to use the Empyrean Avatar. You will need to gather power and summon it again.
				    PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_DEATHBLOW_FAIL, 0);
				}
		    break;
			//Veille's Aetheric Concentrator II
			case 296908:
			    if (player.getLevel() >= 50) {
					announceVeilleII();
					AI2Actions.targetCreature(Veille_Aetheric_ConcentratorAI2.this, getPosition().getWorldMapInstance().getNpc(258200)); //Enraged Veille.
				    AI2Actions.useSkill(Veille_Aetheric_ConcentratorAI2.this, 20108); //Elemental Resistance Aether.
				} else {
					//You have failed to use the Empyrean Avatar. You will need to gather power and summon it again.
				    PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_DEATHBLOW_FAIL, 0);
				}
			break;
			//Veille's Aetheric Concentrator III
			case 296909:
			    if (player.getLevel() >= 50) {
					announceVeilleII();
				    AI2Actions.targetCreature(Veille_Aetheric_ConcentratorAI2.this, getPosition().getWorldMapInstance().getNpc(258200)); //Enraged Veille.
				    AI2Actions.useSkill(Veille_Aetheric_ConcentratorAI2.this, 20109); //Power Aether.
					AI2Actions.useSkill(Veille_Aetheric_ConcentratorAI2.this, 20110); //Magical Aether.
				} else {
					//You have failed to use the Empyrean Avatar. You will need to gather power and summon it again.
				    PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_DEATHBLOW_FAIL, 0);
				}
			break;
		}
	}
	
	private void announceVeilleI() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The first Sphere of Mirage has been activated.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_BUFF_FIRST_OBJECT_ON, 0);
			}
		});
	}
	
	private void announceVeilleII() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The second Sphere of Mirage has been activated. Kaisinel's Agent Veille prepares to cast the Empyrean Lord's blessing.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_BUFF_SECOND_OBJECT_ON, 0);
			}
		});
	}
	
	private void announceVeilleIII() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//You may use the Sphere of Mirage again.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_GODELITE_BUFF_CAN_USE_OBJECT, 120000);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}