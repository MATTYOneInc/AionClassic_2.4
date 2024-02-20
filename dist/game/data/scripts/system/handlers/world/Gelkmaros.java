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
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
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

@WorldID(220070000)
public class Gelkmaros extends GeneralWorldHandler
{
	private Race spawnRace;
	
	protected final int LF4_OWNED_GODELITE = 12119;
	protected final int BN_BUFF_REWARD_HENCH = 20410;
	protected final int HP_SPRING_REGENERATION_L = 18408;
	protected final int HP_SPRING_REGENERATION_D = 18409;
	protected final int DF4_REWARD_NORTHERN_FORTRESS = 12109;
	protected final int DF4_REWARD_SOUTHERN_FORTRESS = 12112;
	protected final int LF4_TAKEN_NORTHERN_FORTRESS = 12115;
	protected final int LF4_TAKEN_SOUTHERN_FORTRESS = 12116;
	protected final int DF4_TAKEN_NORTHERN_FORTRESS = 12117;
	protected final int DF4_TAKEN_SOUTHERN_FORTRESS = 12118;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///HUGE GRYPHU EGG.
			case 217121:
				despawnNpcs(getNpcs(217122));
				spawn(220070000, 217089, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Angry Conchi.
			break;
			///VORGALTEM CITADEL.
			case 257600:
				sendWorldBuff(Race.ELYOS, LF4_TAKEN_NORTHERN_FORTRESS, 1800000 * 1); //Kaisinel's Bane.
				sendWorldBuff(Race.ASMODIANS, DF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Vorgaltem's Favor.
			break;
			case 257605:
				sendWorldBuff(Race.ELYOS, DF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Vorgaltem's Favor.
				sendWorldBuff(Race.ASMODIANS, DF4_TAKEN_NORTHERN_FORTRESS, 1800000 * 1); //Marchutan's Bane.
			break;
			case 257610:
			    if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, DF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Vorgaltem's Favor.
				} else {
					sendWorldBuff(Race.ASMODIANS, DF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Vorgaltem's Favor.
				}
			break;
			///ALTAR OF AVARICE.
			case 257900:
				sendWorldBuff(Race.ELYOS, LF4_TAKEN_SOUTHERN_FORTRESS, 1800000 * 1); //Kaisinel's Boon.
				sendWorldBuff(Race.ASMODIANS, DF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Crimson Favor.
			break;
			case 257905:
				sendWorldBuff(Race.ELYOS, DF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Crimson Favor.
				sendWorldBuff(Race.ASMODIANS, DF4_TAKEN_SOUTHERN_FORTRESS, 1800000 * 1); //Marchutan's Boon.
			break;
			case 257910:
			    if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, DF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Crimson Favor.
				} else {
					sendWorldBuff(Race.ASMODIANS, DF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Crimson Favor.
				}
			break;
			///ENRAGED MASTARIUS.
			case 258205:
				despawnNpcs(getNpcs(296913)); //Mastarius's Aether Concentrator I.
		        despawnNpcs(getNpcs(296914)); //Mastarius's Aether Concentrator II.
		        despawnNpcs(getNpcs(296915)); //Mastarius's Aether Concentrator III.
				//The Agent's Adjutant has appeared.
				sendMsgByRace(1401218, Race.PC_ALL, 5000);
				//The Agent is here, and it is angry!
				sendMsgByRace(1401219, Race.PC_ALL, 10000);
				spawn(220070000, 799095, 1822.0000f, 1976.0000f, 392.0000f, (byte) 48);
				sendWorldBuff(Race.ELYOS, LF4_OWNED_GODELITE, 3600000 * 1); //Veille's Energy.
				sendWorldBuff(Race.ELYOS, BN_BUFF_REWARD_HENCH, 7200000 * 1); //Victory Salute.
			break;
			///BOSS WINDSTREAM.
			case 216846: //Agrima.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(220070000, 281817, 1719.0000f, 2301.0000f, 318.0000f, (byte) 0, 1821);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///ENRAGED MASTARIUS 2.x
			case 258205:
				switch (Rnd.get(1, 4)) {
					case 1:
					    //Marchutan's Agent Mastarius has engaged in battle to defend Gelkmaros.
						sendMsgByRace(1400533, Race.PC_ALL, 10000);
					break;
					case 2:
					    //Marchutan's Agent Mastarius has engaged in battle to defend Gelkmaros.
						sendMsgByRace(1400534, Race.PC_ALL, 10000);
					break;
					case 3:
					    //Marchutan's Agent Mastarius has engaged in battle to defend Gelkmaros.
						sendMsgByRace(1400535, Race.PC_ALL, 10000);
					break;
					case 4:
					    //Marchutan's Agent Mastarius has engaged in battle to defend Gelkmaros.
						sendMsgByRace(1400536, Race.PC_ALL, 10000);
					break;
				}
			break;
			case 799095: ///Mijien <Mastarius Shadow>
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(799095));
					}
				}, 600000);
			break;
			case 281817: ///Ancient Windstream.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						respawnNpc(npc);
						despawnNpcs(getNpcs(281817));
					}
				}, 600000);
			break;
			///https://aioncodex.com/3x/quest/20020/
			case 700700: //Chasing Eye.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(700700));
					}
				}, 60000);
			break;
			///https://aioncodex.com/3x/quest/20024/
			case 216661: //Klawtiar Guardian.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(216661));
					}
				}, 300000);
			break;
			case 217089: //Angry Conchi.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(217089));
					}
				}, 180000);
			break;
			///ILLUSION GATE.
			case 296526:
				spawn(220070000, 296528, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(220070000, 296531, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(220070000, 296532, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296533:
				spawn(220070000, 296535, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(220070000, 296538, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(220070000, 296539, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296540:
				spawn(220070000, 296542, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(220070000, 296544, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(220070000, 296545, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 216546: //Flesh-eating Swarm.
				///Advent Of Flesh-eating Swarm.
				sendMsgByRace(1401820, Race.PC_ALL, 0);
			break;
			case 216547: //Alpha Kuruku.
				///Advent Of Alpha Kuruku.
				sendMsgByRace(1401821, Race.PC_ALL, 0);
			break;
			case 216548: //Alpha Mansat.
				///Advent Of Alpha Mansat.
				sendMsgByRace(1401822, Race.PC_ALL, 0);
			break;
			case 216550: //Camu The Forestkeeper.
				///Advent Of Camu The Forestkeeper.
				sendMsgByRace(1401823, Race.PC_ALL, 0);
			break;
			case 216551: //Deathwing.
				///Advent Of Deathwing.
				sendMsgByRace(1401825, Race.PC_ALL, 0);
			break;
			case 216553: //Chieftain Ulagu.
				///Advent Of Chieftain Ulagu.
				sendMsgByRace(1401826, Race.PC_ALL, 0);
			break;
			case 216554: //Windchaser.
				///Advent Of Windchaser.
				sendMsgByRace(1401824, Race.PC_ALL, 0);
			break;
			case 216557: //Orciphae.
				///Advent Of Orciphae.
				sendMsgByRace(1401827, Race.PC_ALL, 0);
			break;
			case 216561: //Skully Crimsonshell.
				///Advent Of Skully Crimsonshell.
				sendMsgByRace(1401828, Race.PC_ALL, 0);
			break;
			case 216569: //Highpriest Heka.
				///Advent Of Highpriest Heka.
				sendMsgByRace(1401829, Race.PC_ALL, 0);
			break;
			case 216570: //Turatu Spawnfoot.
				///Advent Of Turatu Spawnfoot.
				sendMsgByRace(1401803, Race.PC_ALL, 0);
			break;
			case 216572: //Pincers Prime.
				///Advent Of Pincers Prime.
				sendMsgByRace(1401830, Race.PC_ALL, 0);
			break;
			case 216574: //Hashak The Plough.
				///Advent Of Hashak The Plough.
				sendMsgByRace(1401831, Race.PC_ALL, 0);
			break;
			case 216575: //Elder Malekor.
				///Advent Of Elder Malekor.
				sendMsgByRace(1401832, Race.PC_ALL, 0);
			break;
			case 216576: //Ragnarok.
				///Advent Of Ragnarok.
				sendMsgByRace(1401802, Race.PC_ALL, 0);
			break;
			case 216578: //Gravekeeper Basaim.
				///Advent Of Gravekeeper Basaim.
				sendMsgByRace(1401833, Race.PC_ALL, 0);
			break;
			case 216581: //Minga Bigeyes.
				///Advent Of Minga Bigeyes.
				sendMsgByRace(1401834, Race.PC_ALL, 0);
			break;
			///TREASURE BOX.
			case 216741:
			case 216742:
			case 216743:
			case 216744:
			case 216747:
			case 216748:
			case 216767:
			case 217077:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void onDespawn(Npc npc) {
		switch (npc.getNpcId()) {
			///ENRAGED MASTARIUS 2.x
			case 258205:
				//Marchutan's Agent Mastarius has disappeared.
				sendMsgByRace(1400320, Race.PC_ALL, 0);
    		break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///HUGE GRYPHU EGG.
			case 206138:
			    if (MathUtil.isIn3dRange(npc, player, 8)) {
					despawnNpc(npc);
					respawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					switch (Rnd.get(1, 4)) {
						case 1:
							spawn(220070000, 217121, 1325.6725f, 1623.9979f, 358.44565f, (byte) 58);
							spawn(220070000, 217122, 1301.6289f, 1609.3795f, 361.61570f, (byte) 88);
							spawn(220070000, 217122, 1338.5745f, 1618.8756f, 358.60187f, (byte) 93);
							spawn(220070000, 217122, 1351.5776f, 1627.4027f, 358.94516f, (byte) 92);
						break;
						case 2:
							spawn(220070000, 217122, 1325.6725f, 1623.9979f, 358.44565f, (byte) 58);
							spawn(220070000, 217121, 1338.5745f, 1618.8756f, 358.60187f, (byte) 93);
							spawn(220070000, 217122, 1351.5776f, 1627.4027f, 358.94516f, (byte) 92);
							spawn(220070000, 217122, 1301.6289f, 1609.3795f, 361.61570f, (byte) 88);
						break;
						case 3:
							spawn(220070000, 217122, 1325.6725f, 1623.9979f, 358.44565f, (byte) 58);
							spawn(220070000, 217122, 1338.5745f, 1618.8756f, 358.60187f, (byte) 93);
							spawn(220070000, 217121, 1351.5776f, 1627.4027f, 358.94516f, (byte) 92);
							spawn(220070000, 217122, 1301.6289f, 1609.3795f, 361.61570f, (byte) 88);
						break;
						case 4:
							spawn(220070000, 217122, 1325.6725f, 1623.9979f, 358.44565f, (byte) 58);
							spawn(220070000, 217122, 1338.5745f, 1618.8756f, 358.60187f, (byte) 93);
							spawn(220070000, 217122, 1351.5776f, 1627.4027f, 358.94516f, (byte) 92);
							spawn(220070000, 217121, 1301.6289f, 1609.3795f, 361.61570f, (byte) 88);
						break;
					}
				}
			break;
			///VITALITY WELL.
			case 257798:
			case 258098:
			    if (MathUtil.isIn3dRange(npc, player, 8)) {
					if (!player.getEffectController().hasAbnormalEffect(HP_SPRING_REGENERATION_L)) {
						if (player.getRace() == Race.ELYOS) {
							SkillEngine.getInstance().applyEffectDirectly(HP_SPRING_REGENERATION_L, player, player, 20000 * 1); //Blessing Of Guardian Spring.
						}
					}
				}
			break;
			case 257799:
			case 258099:
			    if (MathUtil.isIn3dRange(npc, player, 8)) {
					if (!player.getEffectController().hasAbnormalEffect(HP_SPRING_REGENERATION_D)) {
						if (player.getRace() == Race.ASMODIANS) {
							SkillEngine.getInstance().applyEffectDirectly(HP_SPRING_REGENERATION_D, player, player, 20000 * 1); //Blessing Of Guardian Spring.
						}
					}
				}
			break;
		}
    }
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			///SPIRITFALL GATE.
			case 730300: //Spiritfall Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						spiritfallGateIn(player, 724.0000f, 2278.0000f, 389.0000f, (byte) 80);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730301: //Spiritfall Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						spiritfallGateOut(player, 729.0000f, 2286.0000f, 389.0000f, (byte) 20);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///SUBTERRANEA GATE.
			case 730302: //Subterranea Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						subterraneaGateIn(player, 2656.0000f, 2194.0000f, 495.0000f, (byte) 73);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730303: //Subterranea Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						subterraneaGateOut(player, 2663.0000f, 2200.0000f, 495.0000f, (byte) 14);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///FOUNTAIN REWARD.
			case 730242: //Gelkmaros Coin Fountain.
			    if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000031, 1)) { //Silver Medal.
						switch (Rnd.get(1, 5)) {
							case 1:
								ItemService.addItem(player, 186000030, Rnd.get(1, 3));
							break;
							case 2:
								ItemService.addItem(player, 186000031, Rnd.get(1, 3));
							break;
							case 3:
								ItemService.addItem(player, 186000096, Rnd.get(1, 3));
							break;
							case 4:
								ItemService.addItem(player, 182202156, Rnd.get(1, 3));
							break;
							case 5:
								ItemService.addItem(player, 182205668, Rnd.get(1, 3));
							break;
						}
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
		PlayerEffectController effectController = player.getEffectController();
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/us/quest/21114/
			case 700729: //Fungie Patch.
				final QuestState qs21114 = player.getQuestStateList().getQuestState(21114); //Poisoned Fungi.
				if (qs21114 != null && qs21114.getStatus() == QuestStatus.START && qs21114.getQuestVarById(0) == 2 &&
				    player.getInventory().decreaseByItemId(182207862, 1)) { //Fungie Spore Liquid.
					ClassChangeService.onUpdateQuest21114(player);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/us/quest/21105/
			case 700812: //Suspicious Wreck.
			    final QuestState qs21105 = player.getQuestStateList().getQuestState(21105); //Cowering Refugee.
				if (qs21105 != null && qs21105.getStatus() == QuestStatus.START && qs21105.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					switch (Rnd.get(1, 2)) {
						case 1:
							spawn(220070000, 799366, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Shulack Refugee.
						break;
						case 2:
							spawn(220070000, 216086, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Naduka Scratcher.
						break;
					}
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/us/quest/20020/
			case 700977: //Naduka Corpse.
				final QuestState qs20020 = player.getQuestStateList().getQuestState(20020); //Crash Of The Dredgion.
				if (qs20020 != null && qs20020.getStatus() == QuestStatus.START && qs20020.getQuestVarById(0) == 2) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182207601, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/us/quest/20022/
			case 700703: //Marayas Wild Grass.
				final QuestState qs20022A = player.getQuestStateList().getQuestState(20022); //Spreading Asmodae Reach.
				if (qs20022A != null && qs20022A.getStatus() == QuestStatus.START && qs20022A.getQuestVarById(0) == 2) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182207605, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/us/quest/20022/
			case 700704: //Mutated Barghest Corpse.
				final QuestState qs20022B = player.getQuestStateList().getQuestState(20022); //Spreading Asmodae Reach.
				if (qs20022B != null && qs20022B.getStatus() == QuestStatus.START && qs20022B.getQuestVarById(0) == 2) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182207607, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/us/quest/20023/
			case 700810: //Daonce's Corpse.
				final QuestState qs20023 = player.getQuestStateList().getQuestState(20023); //Kumbanda's Whereabouts.
				if (qs20023 != null && qs20023.getStatus() == QuestStatus.START && qs20023.getQuestVarById(0) == 2) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182207610, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			case 700772: //Fresh Fruit.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216640, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Rokbak.
			break;
			case 700773: //Soul Invocation Jar.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216628, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Vengeful Romludon.
			break;
			case 700774: //Snuffler Thistle Trap.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216641, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Nimblestump.
			break;
			case 700775: //Worn Spell Scroll.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216629, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sorceress Imilyana.
			break;
			case 700776: //Sandy Carcass.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216642, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Zantra.
			break;
			case 700777: //Vine Seed.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216643, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Vinestem.
			break;
			///SILENTERA CANYON.
			case 730260:
			    if (SiegeService.getInstance().isSiegeInProgress(3011) && SiegeService.getInstance().isSiegeInProgress(3021)) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 27));
				} else {
					underpassIn(player, 480.0000f, 1094.0000f, 334.0000f, (byte) 0);
				}
			break;
			///TELEPORTATION TOWER [Vorgaltem Citadel]
			case 257833:
			case 257834:
			    vorgaltemCitadel(player, 1197.0000f, 826.0000f, 313.0000f, (byte) 30);
			break;
			///TELEPORTATION TOWER [Crimson Temple]
			case 258133:
			case 258134:
			    crimsonTemple(player, 1879.0000f, 1065.0000f, 330.0000f, (byte) 32);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void spiritfallGateIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void spiritfallGateOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void subterraneaGateIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void subterraneaGateOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void underpassIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600010000, 1, x, y, z, h);
	}
	protected void crimsonTemple(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void vorgaltemCitadel(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("GELKMAROS_FORTRESS_220070000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("RHONNAM_REFUGEE_VILLAGE_220070000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("SUBTERRANEA_OBSERVATION_POST_220070000")) {
			switch (player.getRace()) {
				case ELYOS:
				    if (!player.isGM()) {
						player.getController().die();
					}
				break;
			}
		}
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