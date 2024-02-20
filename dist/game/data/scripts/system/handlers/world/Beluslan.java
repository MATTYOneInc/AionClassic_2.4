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
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
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

@WorldID(220040000)
public class Beluslan extends GeneralWorldHandler
{
	private Race spawnRace;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			//Aarien The Seacaller.
			case 280705:
			case 280706:
			case 280707:
			//Queen Alukina.
			case 280713:
			    despawnNpc(npc);
			break;
			///QUEST-MISSION MONSTER.
			///https://aioncodex.com/3x/quest/2060/
			case 700290: //Field Suppressor.
				despawnNpc(npc);
				final QuestState qs2060 = player.getQuestStateList().getQuestState(2060); //Restoring Beluslan Observatory.
				if (qs2060 != null && qs2060.getStatus() == QuestStatus.START && qs2060.getQuestVarById(0) >= 5 && qs2060.getQuestVarById(0) <= 8) {
					spawn(220040000, 213913, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0); //Bakarma Technician.
					spawn(220040000, 213913, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0); //Bakarma Technician.
				}
			break;
			case 213764: //RA-45C.
			    final int strangeObject = spawnRace == Race.ASMODIANS ? 280714 : 280714;
				Npc NpcRace1 = (Npc) spawn(220040000, strangeObject, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				//Something this coming out from my body, kuwaak.
				NpcShoutsService.getInstance().sendMsg(NpcRace1, 341127, NpcRace1.getObjectId(), 0, 0);
            break;
			///https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218858: //Kakuna World
			    AbyssPointsService.addAp(winner, 500);
				//Kakuna of Terror has been defeated. Soon, Kakuna's treasure chest will spawn across the field.
				sendMsgByRace(1401476, Race.ASMODIANS, 0);
				//Kakuna's treasure chest has been spawned.
				sendMsgByRace(1401481, Race.ASMODIANS, 10000);
				//Kakuna of Terror has disappeared.
				sendMsgByRace(1401478, Race.ASMODIANS, 30000);
				//Kakuna of Terror appears in 5 minutes.
				sendMsgByRace(1401480, Race.ASMODIANS, 6900000);
				spawn(220040000, 799989, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0);
				spawn(220040000, 218856, winner.getX(), winner.getY() + 3, winner.getZ(), (byte) 0);
			break;
			//Drink Water Bucket.
			case 700324:
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 213738: //Aarien the Seacaller.
			    ///Advent Of Aarien the Seacaller.
				sendMsgByRace(1401495, Race.PC_ALL, 0);
			break;
			case 213747: //Queen Alukina.
			    ///Advent Of Queen Alukina.
				sendMsgByRace(1401496, Race.PC_ALL, 0);
			break;
			///https://aioncodex.com/3x/quest/2620/
			case 204824: //Gigantic Phagrasul.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204824));
					}
				}, 120000);
			break;
			///KAKUNA WORLD https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218858:
			    ///Kakuna of Terror.
				sendMsgByRace(1401474, Race.ASMODIANS, 60000);
			break;
			case 218856:
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(218856));
						///All of Kakuna's treasure chests are gone.
						sendMsgByRace(1401482, Race.ASMODIANS, 0);
					}
				}, 300000);
			break;
			case 799989:
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(799989));
					}
				}, 300000);
			break;
			///TREASURE BOX.
			case 214132:
			case 214133:
			case 214134:
			case 214135:
			case 214136:
			case 214137:
			case 214138:
			case 214139:
			case 214140:
			case 214141:
			case 214142:
			case 214143:
			case 214144:
			case 214145:
			case 214146:
			case 214147:
			case 214148:
			case 214149:
			case 214150:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/2620/
			case 700323: //Huge Mamut Skull.
				final QuestState qs2620 = player.getQuestStateList().getQuestState(2620); //Summoning Phagrasul.
				if (qs2620 != null && qs2620.getStatus() == QuestStatus.START && qs2620.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182204498, 1)) { //Horn Flute.
					despawnNpc(npc);
				    respawnNpc(npc);
					spawn(220040000, 204824, player.getX(), player.getY(), player.getZ(), (byte) 0); //Gigantic Phagrasul.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2600/
			case 700512: //Gigantic Malek.
				final QuestState qs2600 = player.getQuestStateList().getQuestState(2600); //Humongous Malek.
				if (qs2600 != null && qs2600.getStatus() == QuestStatus.START && qs2600.getQuestVarById(0) == 1 &&
				    player.getInventory().decreaseByItemId(182204528, 1)) { //Kidorun Grave Robber Band's Pick.
					despawnNpc(npc);
				    respawnNpc(npc);
					spawn(220040000, 215383, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Malek Protector.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/4943/
			case 700538: //Light Of Luck.
			    if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs4943 = player.getQuestStateList().getQuestState(4943); //Luck And Persistence.
					if (qs4943 != null && qs4943.getStatus() == QuestStatus.START && qs4943.getQuestVarById(0) == 2) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182207124, 1);
					}
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2042/
			case 730064: //Beluslan Abyss Gate.
				if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs2042 = player.getQuestStateList().getQuestState(2042); //The Last Checkpoint.
					if (qs2042 == null || qs2042.getStatus() != QuestStatus.COMPLETE) {
						///You must first complete the Abyss Entry Quest.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_TELEPORT_TO_ABYSS);
					} else {
						primumLanding(player, 1068.0000f, 2850.0000f, 1636.0000f, (byte) 0);
					}
				}
			break;
			case 730137: //Alukina's Palace Entrance.
				if (player.getLevel() >= 40) {
				    alukinaPalaceIn(player, 593.0000f, 2535.0000f, 289.0000f, (byte) 0);
                } else {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT);
				}
		    break;
			case 730138: //Alukina's Palace Exit.
				alukinaPalaceOut(player, 569.0000f, 2507.0000f, 289.0000f, (byte) 68);
		    break;
			///https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218856: //Kakuna World.
			    if (player.getInventory().decreaseByItemId(185000115, 1)) {
					switch (Rnd.get(1, 7)) {
						case 1:
							ItemService.addItem(player, 188052051, 1);
						break;
						case 2:
							ItemService.addItem(player, 188052053, 1);
						break;
						case 3:
							ItemService.addItem(player, 166000090, 2);
						break;
						case 4:
							ItemService.addItem(player, 166000085, 3);
						break;
						case 5:
							ItemService.addItem(player, 166000080, 4);
						break;
						case 6:
							ItemService.addItem(player, 166000075, 5);
						break;
						case 7:
							ItemService.addItem(player, 166000070, 6);
						break;
					}
				} else {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111300, player.getObjectId(), 2));
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		///QUEST-MISSION ZONES.
		///https://aioncodex.com/3x/quest/2059/
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DF3_SENSORY_AREA_Q2059_220040000")) {
		    final QuestState qs2059 = player.getQuestStateList().getQuestState(2059); //A Peace Offering.
			if (qs2059 != null && qs2059.getStatus() == QuestStatus.START && qs2059.getQuestVarById(0) == 3) {
				despawnNpcs(getNpcs(204806)); //Hoarfrost Survivor.
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("CAMP_KISTENIAN_220040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("RED_MANE_CAVERN_220040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("HOARFROST_SHELTER_220040000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("KIDORUNS_CAMPSITE_220040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("BESFER_REFUGEE_CAMP_220040000")) {
			switch (player.getRace()) {
				case ELYOS:
				    if (!player.isGM()) {
						player.getController().die();
					}
				break;
			}
		}
	}
	
	protected void portEntrance(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void alukinaPalaceOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void alukinaPalaceIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void primumLanding(Player player, float x, float y, float z, byte h) {
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
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