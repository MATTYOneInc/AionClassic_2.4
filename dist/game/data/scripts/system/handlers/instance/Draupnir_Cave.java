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
package instance;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320080000)
public class Draupnir_Cave extends GeneralInstanceHandler
{
	private int adjutantsKilled;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		///You must kill Afrane, Saraswati, Lakshmi, and Nimbarka to make Commander Bakarma appear.
		sendMsgByRace(1400757, Race.PC_ALL, 10000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///Lucky Golden Saam OR Bakarma Mistscale.
		switch (Rnd.get(1, 2)) {
		    case 1:
				final Npc iddf3_aea_lizardasd_47_ae = (Npc) spawn(213390, 175.02058f, 540.7148f, 504.7490f, (byte) 0);
				iddf3_aea_lizardasd_47_ae.getSpawn().setWalkerId("iddf3_dragon_06_mobpath_n_hidden_named_xx");
				WalkManager.startWalking((NpcAI2) iddf3_aea_lizardasd_47_ae.getAi2());
			break;
			case 2:
				final Npc iddf3_pit_goldsaamhiddennmdd_47_ae = (Npc) spawn(213588, 175.02058f, 540.7148f, 504.7490f, (byte) 0);
				iddf3_pit_goldsaamhiddennmdd_47_ae.getSpawn().setWalkerId("iddf3_dragon_06_mobpath_n_hidden_named_xx");
				WalkManager.startWalking((NpcAI2) iddf3_pit_goldsaamhiddennmdd_47_ae.getAi2());
			break;
        }
		///Protector Rakkan OR Bakarma Scaleguard.
		switch (Rnd.get(1, 2)) {
		    case 1:
				final Npc iddf3_lizardfinnamedd_46_Ae = (Npc) spawn(213771, 494.63162f, 434.6142f, 616.0161f, (byte) 0);
				iddf3_lizardfinnamedd_46_Ae.getSpawn().setWalkerId("iddf3_dragon_start_mobpath_n_named_xx");
				WalkManager.startWalking((NpcAI2) iddf3_lizardfinnamedd_46_Ae.getAi2());
			break;
			case 2:
				final Npc iddf3_fea_lizardfid_46_ae = (Npc) spawn(213377, 494.63162f, 434.6142f, 616.0161f, (byte) 0);
				iddf3_fea_lizardfid_46_ae.getSpawn().setWalkerId("iddf3_dragon_start_mobpath_n_named_xx");
				WalkManager.startWalking((NpcAI2) iddf3_fea_lizardfid_46_ae.getAi2());
			break;
        }
		///Hungry Ooze OR Cave Slime.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(213587, 567.0000f, 700.0000f, 538.0000f, (byte) 7); 
			break;
			case 2:
				spawn(213561, 567.0000f, 700.0000f, 538.0000f, (byte) 7); 
			break;
        }
    }
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 213776: //Instructor Afrane.
			case 213778: //Beautiful Lakshmi.
			case 213779: //Commander Nimbarka.
			case 213802: //Kind Saraswati.
				adjutantsKilled++;
				if (adjutantsKilled == 1) {
					///You must kill 3 more Adjutants to make Commander Bakarma appear.
				    sendMsgByRace(1400758, Race.PC_ALL, 0);
				} else if (adjutantsKilled == 2) {
					///You must kill 2 more Adjutants to make Commander Bakarma appear.
				    sendMsgByRace(1400759, Race.PC_ALL, 0);
				} else if (adjutantsKilled == 3) {
					///You must kill 1 more Adjutant to make Commander Bakarma appear.
				    sendMsgByRace(1400760, Race.PC_ALL, 0);
				} else if (adjutantsKilled == 4) {
					///Commander Bakarma has appeared at Beritra's Oracle.
				    sendMsgByRace(1400751, Race.PC_ALL, 0);
					//Deputy Brigade General Yavant.
					despawnNpcs(instance.getNpcs(214026));
					//Commander Bakarma.
					final Npc iddf3_drakanfibossd_50_ah = (Npc) spawn(213780, 744.9534f, 458.35495f, 318.06277f, (byte) 0);
				    iddf3_drakanfibossd_50_ah.getSpawn().setWalkerId("boss_mobpath_n_iddf3_drakanfibossd_50_ah");
				    WalkManager.startWalking((NpcAI2) iddf3_drakanfibossd_50_ah.getAi2());
				}
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700296: //Bloodfury Booster.
				final QuestState qs2633 = player.getQuestStateList().getQuestState(2633); //Destroying Balaur Weapons.
				if (qs2633 != null && qs2633.getStatus() == QuestStatus.START && qs2633.getQuestVarById(0) == 1 &&
				    player.getInventory().decreaseByItemId(182204497, 1)) { //Disturbing Stone.
					ClassChangeService.onUpdateQuest2633(player);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			case 700514: //Artifact Of Sealing.
				final QuestState qs4912 = player.getQuestStateList().getQuestState(4912); //[Group] The Curse Of Agrif's Rage.
				if (qs4912 != null && qs4912.getStatus() == QuestStatus.START && qs4912.getQuestVarById(0) == 0) {
					despawnNpc(npc);
					spawn(215385, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Garnokk.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
}