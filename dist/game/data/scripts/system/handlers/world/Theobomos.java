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
package world;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@WorldID(210060000)
public class Theobomos extends GeneralWorldHandler
{
	private Race spawnRace;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///https://aioncodex.com/3x/quest/1092/
			case 700390: //Flame Eternal.
			    despawnNpc(npc);
			    final QuestState qs1092 = player.getQuestStateList().getQuestState(1092); //Josnack's Dilemma.
				if (qs1092 != null && qs1092.getStatus() == QuestStatus.START && qs1092.getQuestVarById(0) == 4) {
					spawn(210060000, 214552, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0);
					spawn(210060000, 214552, winner.getX(), winner.getY() + 3, winner.getZ(), (byte) 0);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			case 213906: //Treasure Box.
			case 214024: //Treasure Box.
			    final int dauntlessProtector = spawnRace == Race.ELYOS ? 280722 : 280722;
				Npc NpcRace1 = (Npc) spawn(210060000, dauntlessProtector, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				//Yaap!
				NpcShoutsService.getInstance().sendMsg(NpcRace1, 341026, NpcRace1.getObjectId(), 0, 0);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/3082/
			case 700417: //Magic Ward Of Coldness.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(700417));
					}
				}, 120000);
			break;
			///TREASURE BOX.
			case 213904:
			case 213906:
			case 213918:
			case 213919:
			case 213920:
			case 213921:
			case 213922:
			case 213923:
			case 213924:
			case 214024:
			case 214025:
			case 214622:
			case 214623:
			case 214624:
			case 214625:
			case 214626:
			case 214627:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/3036/
			case 700398: //Unidentified Artifact.
				final QuestState qs3036 = player.getQuestStateList().getQuestState(3036); //Let See What It Does.
				if (qs3036 != null && qs3036.getStatus() == QuestStatus.START && qs3036.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182208026, 1)) { //Recharged Activation Stone.
					ClassChangeService.onUpdateQuest3036(player);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/3082/
			case 700416: //Fire Core.
				final QuestState qs3082 = player.getQuestStateList().getQuestState(3082); //Dousing The Flame.
				if (qs3082 != null && qs3082.getStatus() == QuestStatus.START && qs3082.getQuestVarById(0) == 2 &&
				    player.getInventory().decreaseByItemId(182208060, 1)) { //Magic Ward Scroll.
					despawnNpc(npc);
				    respawnNpc(npc);
					ClassChangeService.onUpdateQuest3082(player);
					spawn(210060000, 700417, 390.0000f, 2538.0000f, 57.0000f, (byte) 0, 942); //Magic Ward Of Coldness.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/3939/
			case 700537: //Tears Of Luck.
				if (player.getRace() == Race.ELYOS) {
					final QuestState qs3939 = player.getQuestStateList().getQuestState(3939); //Persistence And Luck.
					if (qs3939 != null && qs3939.getStatus() == QuestStatus.START && qs3939.getQuestVarById(0) == 2) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182206098, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
			break;
			///https://aioncodex.com/3x/quest/3060/
			case 730148: //Red Journal.
			    despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182208043, 1);
			break;
			///https://aioncodex.com/3x/quest/3049/
			case 730149: //Plumis' Corpse
			    despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182208034, 1);
			break;
			///Dried-Out Vine Teleport.
			case 730169:
			    driedOutVine1(player, 2456.042f, 2388.615f, 32.53795f, (byte) 31);
			break;
			case 730170:
				driedOutVine2(player, 2841.572f, 2500.023f, 40.20959f, (byte) 39);
			break;
			case 730171:
				driedOutVine3(player, 1971.371f, 2676.447f, 61.50000f, (byte) 49);
			break;
			case 730172:
				driedOutVine4(player, 2254.681f, 2839.995f, 58.37074f, (byte) 46);
			break;
			case 730173:
				driedOutVine5(player, 2674.758f, 2947.456f, 37.47572f, (byte) 44);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void driedOutVine1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void driedOutVine2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void driedOutVine3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void driedOutVine4(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void driedOutVine5(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void respawnNpc(Npc npc) {
	    if (npc != null) {
            npc.getController().scheduleRespawn();
        }
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	protected List<Npc> getNpcs(int npcId) {
        List<Npc> npcs = new FastList<Npc>();
        for (Npc npc : this.map.getWorld().getNpcs()) {
            if (npc.getNpcId() == npcId) {
                npcs.add(npc);
            }
        }
        return npcs;
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getWorldId() == map.getMapId() && player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
}