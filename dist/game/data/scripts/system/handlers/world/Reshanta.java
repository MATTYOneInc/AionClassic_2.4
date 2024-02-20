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
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.*;
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

@WorldID(400010000)
public class Reshanta extends GeneralWorldHandler
{
	protected final int ABYSS_REWARD_WEASTERN_SIEL = 12070;
	protected final int ABYSS_REWARD_EASTERN_SIEL = 12071;
	protected final int ABYSS_REWARD_SULFUR_TREE = 12072;
	protected final int ABYSS_REWARD_ANCIENT_RHOO = 12073;
	protected final int ABYSS_REWARD_ASTERIA = 12074;
	protected final int ABYSS_REWARD_CROTAN = 12075;
	protected final int ABYSS_REWARD_DKISAS = 12076;
	protected final int ABYSS_REWARD_LAMIREN = 12077;
	protected final int HP_SPRING_REGENERATION_L = 18408;
	protected final int HP_SPRING_REGENERATION_D = 18409;
	protected final int ACH_STATUP_QUARTERMASTER = 18145;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///SIEL WESTERN FORTRESS.
			case 263001:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_WEASTERN_SIEL, 1200000 * 1);
			break;
			case 263006:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_WEASTERN_SIEL, 1200000 * 1);
			break;
			case 263011:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_WEASTERN_SIEL, 1200000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_WEASTERN_SIEL, 1200000 * 1);
				}
			break;
			///SIEL EASTERN FORTRESS.
			case 263301:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_EASTERN_SIEL, 1200000 * 1);
			break;
			case 263306:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_EASTERN_SIEL, 1200000 * 1);
			break;
			case 263311:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_EASTERN_SIEL, 1200000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_EASTERN_SIEL, 1200000 * 1);
				}
			break;
			///SULFUR FORTRESS.
			case 264501:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_SULFUR_TREE, 1200000 * 1);
			break;
			case 264506:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_SULFUR_TREE, 1200000 * 1);
			break;
			case 264511:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_SULFUR_TREE, 1200000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_SULFUR_TREE, 1200000 * 1);
				}
			break;
			///ROAH FORTRESS.
			case 266301:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_ANCIENT_RHOO, 1200000 * 1);
			break;
			case 266306:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_ANCIENT_RHOO, 1200000 * 1);
			break;
			case 266311:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_ANCIENT_RHOO, 1800000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_ANCIENT_RHOO, 1800000 * 1);
				}
			break;
			///KROTAN REFUGE.
			case 267801:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_CROTAN, 1200000 * 1);
			break;
			case 267806:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_CROTAN, 1200000 * 1);
			break;
			case 267811:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_CROTAN, 1800000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_CROTAN, 1800000 * 1);
				}
			break;
			///KYSIS FORTRESS.
			case 269001:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_DKISAS, 1200000 * 1);
			break;
			case 269006:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_DKISAS, 1200000 * 1);
			break;
			case 269011:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_DKISAS, 1800000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_DKISAS, 1800000 * 1);
				}
			break;
			///MIREN FORTRESS.
			case 269901:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_LAMIREN, 1200000 * 1);
			break;
			case 269906:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_LAMIREN, 1200000 * 1);
			break;
			case 269911:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_LAMIREN, 1800000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_LAMIREN, 1800000 * 1);
				}
			break;
			///ASTERIA FORTRESS.
			case 270801:
			    sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_ASTERIA, 1200000 * 1);
			break;
			case 270806:
			    sendWorldBuff(Race.ELYOS, ABYSS_REWARD_ASTERIA, 1200000 * 1);
			break;
			case 270811:
				if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, ABYSS_REWARD_ASTERIA, 1800000 * 1);
				} else {
					sendWorldBuff(Race.ASMODIANS, ABYSS_REWARD_ASTERIA, 1800000 * 1);
				}
			break;
			case 251001: //Menotios.
				spawn(400010000, 290116, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
			break;
			///ENTAGED FRAGMENT MENOTIOS 2.x
			case 218982: //Enraged Fragment Menotios.
			    despawnNpc(npc);
				spawn(400010000, 218989, npc.getX() + 8, npc.getY(), npc.getZ(), (byte) 0);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 251001: //Menotios.
			    ///Advent Of Menotios.
				sendMsgByRace(1401497, Race.PC_ALL, 0);
			break;
			case 290116: //Aetherback Titan Core.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(290116));
					}
				}, 60000);
			break;
			///https://aioncodex.com/3x/quest/1075/
			case 214102: //Ranx Archer.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(214102));
					}
				}, 180000);
			break;
			case 290118: //Queen's Egg [Queen Klawtinel]
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(290118));
						spawn(400010000, 290119, npc.getX() + 2, npc.getY(), npc.getZ(), (byte) 0);
						spawn(400010000, 290119, npc.getX(), npc.getY() + 2, npc.getZ(), (byte) 0);
						spawn(400010000, 290119, npc.getX() - 2, npc.getY(), npc.getZ(), (byte) 0);
					}
				}, 5000);
			break;
			///ILLUSION GATE.
			///Siel's E-W, Sulfur Fortress.
			case 295125:
				spawn(400010000, 294880, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 294882, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 294883, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 295126:
				spawn(400010000, 294884, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 294886, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 294887, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 295127:
				spawn(400010000, 294888, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 294890, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 294891, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			///Divine Fortress.
			case 295212:
				spawn(400010000, 295185, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 295187, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 295189, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 295213:
				spawn(400010000, 295190, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 295192, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 295194, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			///Roah, Krotan, Kysis, Miren & Asteria Fortress.
			case 296058:
				spawn(400010000, 296040, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 296042, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 296043, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296059:
				spawn(400010000, 296044, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 296046, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 296047, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296060:
				spawn(400010000, 296048, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(400010000, 296050, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(400010000, 296051, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			///TREASURE BOX.
			case 253734:
			case 253735:
			case 253736:
			case 253737:
			case 253738:
			case 254574:
			case 256670:
			case 256671:
			case 256672:
			case 256673:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///VITALITY WELL.
			case 275051:
			case 275121:
			case 275221:
			case 275418:
			case 276027:
			case 276227:
			case 276324:
			case 276424:
			case 276521:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					if (!player.getEffectController().hasAbnormalEffect(HP_SPRING_REGENERATION_L)) {
						if (player.getRace() == Race.ELYOS) {
							SkillEngine.getInstance().applyEffectDirectly(HP_SPRING_REGENERATION_L, player, player, 20000 * 1);
						}
					}
				}
			break;
			case 275052:
			case 275122:
			case 275222:
			case 275419:
			case 276028:
			case 276228:
			case 276325:
			case 276425:
			case 276522:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					if (!player.getEffectController().hasAbnormalEffect(HP_SPRING_REGENERATION_D)) {
						if (player.getRace() == Race.ASMODIANS) {
							SkillEngine.getInstance().applyEffectDirectly(HP_SPRING_REGENERATION_D, player, player, 20000 * 1);
						}
					}
				}
			break;
			///QUARTERMASTER.
			case 263261:
			case 263563:
			case 264763:
			case 266548:
			case 268048:
			case 269246:
			case 270146:
			    if (MathUtil.isIn3dRange(npc, player, 15)) {
					if (!player.getEffectController().hasAbnormalEffect(ACH_STATUP_QUARTERMASTER)) {
						if (player.getRace() == Race.ELYOS) {
							SkillEngine.getInstance().applyEffectDirectly(ACH_STATUP_QUARTERMASTER, player, player, 1200000 * 1);
						}
					}
				}
			break;
			case 263262:
			case 263564:
			case 264764:
			case 266549:
			case 268049:
			case 269247:
			case 270147:
			    if (MathUtil.isIn3dRange(npc, player, 15)) {
					if (!player.getEffectController().hasAbnormalEffect(ACH_STATUP_QUARTERMASTER)) {
						if (player.getRace() == Race.ASMODIANS) {
							SkillEngine.getInstance().applyEffectDirectly(ACH_STATUP_QUARTERMASTER, player, player, 1200000 * 1);
						}
					}
				}
			break;
		}
    }
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			///FOUNTAIN REWARD.
			case 730142: //Teminon Coin Fountain.
			case 730143: //Primum Coin Fountain.
			    if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000031, 1)) { //Silver Medal.
						switch (Rnd.get(1, 4)) {
							case 1:
								ItemService.addItem(player, 186000030, Rnd.get(1, 3));
							break;
							case 2:
								ItemService.addItem(player, 186000096, Rnd.get(1, 3));
							break;
							case 3:
								ItemService.addItem(player, 182202156, 1);
							break;
							case 4:
								ItemService.addItem(player, 182205668, 1);
							break;
						}
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///ENTAGED FRAGMENT MENOTIOS 2.x
			case 218982: //Enraged Fragment Menotios.
			    if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(185000117, 1)) {
						despawnNpc(npc);
						respawnNpc(npc);
						///Enraged Fragment Menotios will be unbound in 5 minutes.
						sendMsgByRace(1401591, Race.PC_ALL, 0);
						///Enraged Fragment Menotios will be soon unbound.
						sendMsgByRace(1401597, Race.PC_ALL, 170000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								///Enraged Fragment Menotios has been unbound.
								sendMsgByRace(1401592, Race.PC_ALL, 0);
								spawn(400010000, 218984, npc.getX(), npc.getY(), npc.getZ(), (byte) 101); //Enraged Fragment Menotios.
							}
						}, 300000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								despawnNpcs(getNpcs(218983));
								despawnNpcs(getNpcs(218984));
								despawnNpcs(getNpcs(218985));
							}
						}, 3600000);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/us/quest/1074-2074/
			case 700355: //Artifact Of The Inception.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs1074 = player.getQuestStateList().getQuestState(1074); //Fragment Of Memory.
					if (qs1074 != null && qs1074.getStatus() == QuestStatus.START && qs1074.getQuestVarById(0) == 3 && player.getInventory().decreaseByItemId(188020000, 1)) {
						ClassChangeService.onUpdateMission1074(player);
						PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 271));
					} else {
						///I need a Temporal Stone!
					    PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111203, player.getObjectId(), 2));
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs2074 = player.getQuestStateList().getQuestState(2074); //Looking For Leibo.
					if (qs2074 != null && qs2074.getStatus() == QuestStatus.START && qs2074.getQuestVarById(0) == 5 && player.getInventory().decreaseByItemId(188020000, 1)) {
						ClassChangeService.onUpdateMission2074(player);
						PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 291));
					} else {
						///I need a Temporal Stone!
					    PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111203, player.getObjectId(), 2));
					}
				}
			break;
			///https://aioncodex.com/us/quest/1099-2099/
			case 700551: //Fissure Of Destiny.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs1099 = player.getQuestStateList().getQuestState(1099);
				    if (qs1099 != null && qs1099.getStatus() == QuestStatus.START && qs1099.getQuestVarById(0) == 1) {
						WorldMapInstance ataxiarA = InstanceService.getNextAvailableInstance(310120000);
						InstanceService.registerPlayerWithInstance(ataxiarA, player);
						TeleportService2.teleportTo(player, 310120000, ataxiarA.getInstanceId(), 52.0000f, 174.0000f, 229.0000f, (byte) 0);
					} else {
						///You cannot move to that destination.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs2099 = player.getQuestStateList().getQuestState(2099);
					if (qs2099 != null && qs2099.getStatus() == QuestStatus.START && qs2099.getQuestVarById(0) == 1) {
						WorldMapInstance ataxiarD = InstanceService.getNextAvailableInstance(320140000);
					    InstanceService.registerPlayerWithInstance(ataxiarD, player);
					    TeleportService2.teleportTo(player, 320140000, ataxiarD.getInstanceId(), 452.0000f, 420.0000f, 230.0000f, (byte) 75);
					} else {
						///You cannot move to that destination.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
			break;
			///https://aioncodex.com/us/quest/19509-29509/
			case 701419: //Pile Of Dirt.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs19509A = player.getQuestStateList().getQuestState(19509);
				    if (qs19509A!= null && qs19509A.getStatus() == QuestStatus.START && qs19509A.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215154, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs29509A = player.getQuestStateList().getQuestState(29509);
					if (qs29509A != null && qs29509A.getStatus() == QuestStatus.START && qs29509A.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215155, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
			break;
			case 701420: //Pile Of Dirt.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs19509B = player.getQuestStateList().getQuestState(19509);
				    if (qs19509B != null && qs19509B.getStatus() == QuestStatus.START && qs19509B.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215156, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs29509B = player.getQuestStateList().getQuestState(29509);
					if (qs29509B != null && qs29509B.getStatus() == QuestStatus.START && qs29509B.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215157, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
			break;
			case 701421: //Pile Of Dirt.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs19509C = player.getQuestStateList().getQuestState(19509);
				    if (qs19509C != null && qs19509C.getStatus() == QuestStatus.START && qs19509C.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215158, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs29509C = player.getQuestStateList().getQuestState(29509);
					if (qs29509C != null && qs29509C.getStatus() == QuestStatus.START && qs29509C.getQuestVarById(0) == 0) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182215159, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
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
		effectController.removeEffect(ACH_STATUP_QUARTERMASTER);
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
	
	protected void sendWorldMsg(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
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

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {

	}
}