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

@WorldID(210040000)
public class Heiron extends GeneralWorldHandler
{
	private int klawspawn;
	private Race spawnRace;
	
	protected final int BNMA_SHIELD_LASBERG = 18108;
	protected final int BNAS_PRV_POISON_GAS = 17820;
	protected final int BLOOD_CELL_DESTRUCTION = 18037;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/1053-1548/
			case 700209: //Klawspawn.
                klawspawn++;
				despawnNpc(npc);
				final QuestState qs1053 = player.getQuestStateList().getQuestState(1053); //The Klaw Threat.
				if (qs1053 != null && qs1053.getStatus() == QuestStatus.START && qs1053.getQuestVarById(0) == 3 && klawspawn == 3) {
					klawspawn = 0;
					spawn(210040000, 212120, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0); //Queen Taklaw.
				} else if (klawspawn == 3) {
					klawspawn = 0;
					spawn(210040000, 212120, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0); //Queen Taklaw.
				}
            break;
			case 212151: //Chairman Garnis.
			    final int garnisianSpawn = spawnRace == Race.ELYOS ? 280793 : 280793;
				Npc NpcRace1 = (Npc) spawn(210040000, garnisianSpawn, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				//Don't think this is the end!
				NpcShoutsService.getInstance().sendMsg(NpcRace1, 340395, NpcRace1.getObjectId(), 0, 0);
            break;
			///https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218855: //Kakuna World.
			    AbyssPointsService.addAp(winner, 500);
				//Kakuna of Terror has been defeated. Soon, Kakuna's treasure chest will spawn across the field.
				sendMsgByRace(1401476, Race.ELYOS, 0);
				//Kakuna's treasure chest has been spawned.
				sendMsgByRace(1401481, Race.ELYOS, 10000);
				//Kakuna of Terror has disappeared.
				sendMsgByRace(1401478, Race.ELYOS, 30000);
				//Kakuna of Terror appears in 5 minutes.
				sendMsgByRace(1401480, Race.ELYOS, 6900000);
				spawn(210040000, 799987, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0);
				spawn(210040000, 218856, winner.getX(), winner.getY() + 3, winner.getZ(), (byte) 0);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 204655: //Bollvig The Archon Of Storm.
			    SkillEngine.getInstance().applyEffectDirectly(BNMA_SHIELD_LASBERG, npc, npc, 0); //Storm Curtain.
			break;
			case 212008: //Akairun Of Medeus.
			    SkillEngine.getInstance().applyEffectDirectly(BNAS_PRV_POISON_GAS, npc, npc, 0); //Virulent Infection.
			break;
			case 212314: //Bollvig Blackheart.
			    ///Advent Of Bollvig Blackheart.
				sendMsgByRace(1401491, Race.PC_ALL, 0);
			    SkillEngine.getInstance().applyEffectDirectly(BLOOD_CELL_DESTRUCTION, npc, npc, 0); //Blood Cell Destruction.
			break;
			case 211265: //Medeus The Vile.
			    ///Advent Of Medeus The Vile.
				sendMsgByRace(1401490, Race.PC_ALL, 0);
			break;
			case 212281: //Guardian Vingeveu.
			    ///Advent Of Guardian Vingeveu.
				sendMsgByRace(1401492, Race.PC_ALL, 0);
			break;
			case 212282: //Bulwark Jeshuchi.
			    ///Advent Of Bulwark Jeshuchi.
				sendMsgByRace(1401493, Race.PC_ALL, 0);
			break;
			case 212283: //Watcher Zapiel.
			    ///Advent Of Watcher Zapiel.
				sendMsgByRace(1401494, Race.PC_ALL, 0);
			break;
			///https://aioncodex.com/3x/quest/1647/
			case 204635: //Graveknight.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204635));
					}
				}, 180000);
			break;
			///https://aioncodex.com/3x/quest/1055/
			case 204623: //Released Soul.
			case 204624: //Released Soul.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204623));
						despawnNpcs(getNpcs(204624));
					}
				}, 60000);
			break;
			///KAKUNA WORLD https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218855:
			    ///Kakuna of Terror has appeared.
				sendMsgByRace(1401474, Race.ELYOS, 60000);
			break;
			case 218856:
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(218856));
						///All of Kakuna's treasure chests are gone.
						sendMsgByRace(1401482, Race.ELYOS, 0);
					}
				}, 300000);
			break;
			case 799987:
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(799987));
					}
				}, 300000);
			break;
			///TREASURE BOX.
			case 212332:
			case 212333:
			case 212336:
			case 212337:
			case 212338:
			case 212339:
			case 212340:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700239: ///Drake Stone Statue.
				despawnNpc(npc);
				respawnNpc(npc);
				spawn(210040000, 212008, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Akairun Of Medeus.
		    break;
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/1562/
			case 700193: //Tombstone Of Litonos.
				final QuestState qs1562 = player.getQuestStateList().getQuestState(1562); //Crossed Destiny.
				if (qs1562 != null && qs1562.getStatus() == QuestStatus.START && qs1562.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ClassChangeService.onUpdateQuest1562(player);
					spawn(210040000, 204616, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Litonos.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/1647/
			case 700272: //Suspicious Stone Statue.
				final QuestState qs1647 = player.getQuestStateList().getQuestState(1647); //Dressing Up For Bollvig.
				if (qs1647 != null && qs1647.getStatus() == QuestStatus.START && qs1647.getQuestVarById(0) == 0 &&
				    !player.getEquipment().getEquippedItemsByItemId(110100150).isEmpty() &&
					!player.getEquipment().getEquippedItemsByItemId(111100140).isEmpty() &&
					!player.getEquipment().getEquippedItemsByItemId(112100124).isEmpty() &&
					!player.getEquipment().getEquippedItemsByItemId(113100144).isEmpty() &&
					!player.getEquipment().getEquippedItemsByItemId(114100149).isEmpty()) {
					PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 199));
					ClassChangeService.onUpdateQuest1647(player);
				} else {
					///I'll put on some clothes like Myanee wore.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111060, player.getObjectId(), 2));
				}
			break;
			///https://aioncodex.com/3x/quest/1559/
			case 700513: //Old Box.
				despawnNpc(npc);
				respawnNpc(npc);
			    ItemService.addItem(player, 182201823, 1);
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
			case 730061: //Heiron Abyss Gate.
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
			case 730040: ///Secret Passage Of Indratu Legion.
				if (player.getLevel() >= 45) {
				    indratuLegionIn(player, 2599.0000f, 2182.0000f, 154.0000f, (byte) 27);
                } else {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT);
				}
		    break;
			///https://aion.plaync.com/board/clsnotice/view?articleId=1064222
			case 218856: //Kakuna World
			    if (player.getInventory().decreaseByItemId(185000115, 1)) {
					switch (Rnd.get(1, 7)) {
						case 1:
							ItemService.addItem(player, 188052050, 1);
						break;
						case 2:
							ItemService.addItem(player, 188052052, 1);
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
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("ARBOLUS_HAVEN_210040000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("SENEAS_CAMPSITE_210040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("NEW_HEIRON_GATE_210040000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("JEIAPARAN_VILLAGE_210040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("HEIRON_OBSERVATORY_210040000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("CHANGARNERKS_CAMPSITE_210040000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("KISHAR_OBSERVATION_POST_210040000")) {
			switch (player.getRace()) {
				case ASMODIANS:
				    if (!player.isGM()) {
						player.getController().die();
					}
				break;
			}
		}
	}
	
	protected void indratuLegionIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
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
	
	@Override
	public void onLeaveWorld(Player player) {
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(1860); //Trajanus's Blessing.
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