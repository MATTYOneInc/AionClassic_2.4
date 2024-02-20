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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
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

@WorldID(210020000)
public class Eltnen extends GeneralWorldHandler
{
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///BOSS.
			case 211805: //Kratia.
			    despawnNpc(npc);
			    spawn(210020000, 211812, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Harpback.
			break;
			///MYSTERIOUS CRATE.
			case 211801:
			    despawnNpc(npc);
			    switch (Rnd.get(1, 6)) {
				    case 1:
						String failMsg1 = winner.getName() + " was unlucky and spawned a seedy elroco";
						PacketSendUtility.sendWhiteMessage(winner, failMsg1);
						spawn(210020000, 211792, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Elroco.
					break;
					case 2:
					    String muMuBoss = winner.getName() + " revealed [MuMu's Boss] by knocking on Mysterious Crate";
						PacketSendUtility.sendWhiteMessage(winner, muMuBoss);
						spawn(210020000, 211793, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0); //MuMu Mon.
						spawn(210020000, 211794, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0); //MuMu Zoo.
					break;
					case 3:
					    String cursedBoss = winner.getName() + " revealed [Cursed's Boss] by knocking on Mysterious Crate";
						PacketSendUtility.sendWhiteMessage(winner, cursedBoss);
						spawn(210020000, 211795, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0); //Cursed Camu.
						spawn(210020000, 211797, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0); //Cursed Muku.
						spawn(210020000, 211796, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0); //Cursed Miku.
					break;
					case 4:
					    String arrogantAmurru = winner.getName() + " revealed [Arrogant Amurru] by knocking on Mysterious Crate";
						PacketSendUtility.sendWhiteMessage(winner, arrogantAmurru);
					    spawn(210020000, 211798, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Arrogant Amurru.
					break;
					case 5:
						String failMsg2 = winner.getName() + " was unlucky and spawned a seedy oozing clodworm";
						PacketSendUtility.sendWhiteMessage(winner, failMsg2);
						spawn(210020000, 211799, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Oozing Clodworm.
					break;
					case 6:
					    String chaosDracus = winner.getName() + " revealed [Chaos Dracus] by knocking on Mysterious Crate";
						PacketSendUtility.sendWhiteMessage(winner, chaosDracus);
					    spawn(210020000, 211800, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Chaos Dracus.
					break;
				}
			break;
        }
    }
	
	@Override
	public void onSpawn(final Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/2484/
			case 203331: //Hippolytus.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(203331));
					}
				}, 120000);
			break;
			///https://aioncodex.com/3x/quest/1038/
			case 204005: //Hippolyte.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204005));
					}
				}, 180000);
			break;
			///https://aioncodex.com/3x/quest/1038/
			case 204025: //Protector Kheldon.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204025));
					}
				}, 60000);
			break;
			///https://aioncodex.com/3x/quest/1371/
			case 700215: //Bouquet.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(700215));
					}
				}, 60000);
			break;
			case 280327: //Queen's Egg [Queen Klawtaka]
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(280327));
						spawn(210020000, 280326, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Larve.
					}
				}, 5000);
			break;
			case 211040: //Grand Chieftain Saendukal.
			    ///Advent Of Grand Chieftain Saendukal.
				sendMsgByRace(1401486, Race.PC_ALL, 0);
			    SkillEngine.getInstance().applyEffectDirectly(16873, npc, npc, 0); //Bellicosity.
			break;
			case 211041: //Andre.
			    ///Advent Of Andre.
				sendMsgByRace(1401484, Race.PC_ALL, 0);
			break;
			case 211700: //Summoner Klacha.
			    ///Advent Of Summoner Klacha.
				sendMsgByRace(1401485, Race.PC_ALL, 0);
			break;
			///TREASURE BOX.
			case 211643:
			case 211644:
			case 211858:
			case 211859:
			case 211860:
			case 211861:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/2484/
			case 700267: //Secret Beacon Mound.
				final QuestState qs2484 = player.getQuestStateList().getQuestState(2484); //Our Man In Elysea.
				if (qs2484 != null && qs2484.getStatus() == QuestStatus.START && qs2484.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182204205, 1)) { //Red Hay.
					despawnNpc(npc);
				    respawnNpc(npc);
					ClassChangeService.onUpdateQuest2484(player);
					PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 39));
					spawn(210020000, 203331, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Hippolytus.
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
			///https://aioncodex.com/3x/quest/1044/
			case 730060: //Eltnen Abyss Gate.
				if (player.getRace() == Race.ELYOS) {
					final QuestState qs1044 = player.getQuestStateList().getQuestState(1044); //Testing Flight Skills.
					if (qs1044 == null || qs1044.getStatus() != QuestStatus.COMPLETE) {
						///You must first complete the Abyss Entry Quest.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_TELEPORT_TO_ABYSS);
					} else {
						teminonLanding(player, 2923.0000f, 972.0000f, 1538.0000f, (byte) 0);
					}
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		///QUEST-MISSION ZONES.
		///https://aioncodex.com/3x/quest/1041/
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("LF2_SENSORY_AREA_Q1041_A_210020000")) {
		    final QuestState qs1041A = player.getQuestStateList().getQuestState(1041); //A Dangerous Artifact.
			if (qs1041A != null && qs1041A.getStatus() == QuestStatus.START && qs1041A.getQuestVarById(0) == 2) {
				despawnNpcs(getNpcs(204015));
				despawnNpcs(getNpcs(700179));
				ClassChangeService.onUpdateMission1041(player);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Civil Engineer.
						spawn(210020000, 204015, 2279.9400f, 2551.8500f, 261.8840f, (byte) 25);
						spawn(210020000, 204015, 2378.9200f, 2544.0900f, 266.0000f, (byte) 45);
						spawn(210020000, 204015, 1941.0300f, 2370.1400f, 278.2090f, (byte) 15);
						spawn(210020000, 204015, 2059.9387f, 2284.1523f, 280.7443f, (byte) 30);
						spawn(210020000, 204015, 2171.9812f, 2623.3300f, 273.7960f, (byte) 31);
						///Wooden Prison.
						spawn(210020000, 700176, 2059.9102f, 2283.9336f, 280.46469f, (byte) 0, 399);
						spawn(210020000, 700176, 2379.0000f, 2544.0000f, 264.62933f, (byte) 0, 400);
						spawn(210020000, 700176, 1941.0000f, 2370.0000f, 276.24060f, (byte) 0, 401);
						spawn(210020000, 700176, 2280.0000f, 2552.0000f, 261.80701f, (byte) 0, 406);
						spawn(210020000, 700176, 2172.0000f, 2623.0000f, 273.26801f, (byte) 0, 407);
					}
				}, 5000);
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("LF2_SENSORY_AREA_Q1041_B_210020000")) {
			final QuestState qs1041B = player.getQuestStateList().getQuestState(1041); //A Dangerous Artifact.
			if (qs1041B != null && qs1041B.getStatus() == QuestStatus.START && qs1041B.getQuestVarById(0) == 2) {
				despawnNpcs(getNpcs(204015));
				despawnNpcs(getNpcs(700179));
				ClassChangeService.onUpdateMission1041(player);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Civil Engineer.
						spawn(210020000, 204015, 2279.9400f, 2551.8500f, 261.8840f, (byte) 25);
						spawn(210020000, 204015, 2378.9200f, 2544.0900f, 266.0000f, (byte) 45);
						spawn(210020000, 204015, 1941.0300f, 2370.1400f, 278.2090f, (byte) 15);
						spawn(210020000, 204015, 2059.9387f, 2284.1523f, 280.7443f, (byte) 30);
						spawn(210020000, 204015, 2171.9812f, 2623.3300f, 273.7960f, (byte) 31);
						///Wooden Prison.
						spawn(210020000, 700176, 2059.9102f, 2283.9336f, 280.46469f, (byte) 0, 399);
						spawn(210020000, 700176, 2379.0000f, 2544.0000f, 264.62933f, (byte) 0, 400);
						spawn(210020000, 700176, 1941.0000f, 2370.0000f, 276.24060f, (byte) 0, 401);
						spawn(210020000, 700176, 2280.0000f, 2552.0000f, 261.80701f, (byte) 0, 406);
						spawn(210020000, 700176, 2172.0000f, 2623.0000f, 273.26801f, (byte) 0, 407);
					}
				}, 5000);
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("AGAIRON_VILLAGE_210020000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("NOVANS_CROSSING_210020000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("ELTNEN_OBSERVATORY_210020000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("GOLDEN_BOUGH_GARRISON_210020000")) {
			switch (player.getRace()) {
				case ASMODIANS:
				    if (!player.isGM()) {
						player.getController().die();
					}
				break;
			}
		}
	}
	
	protected void teminonLanding(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 400010000, 1, x, y, z, h);
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
	
	public void sendWorldBuff(final Race race, final int buffId, final int duration) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.getWorldId() == map.getMapId() && player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
					SkillEngine.getInstance().applyEffectDirectly(buffId, player, player, duration);
				}
			}
		});
	}
}