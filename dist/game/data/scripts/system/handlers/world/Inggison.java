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

@WorldID(210050000)
public class Inggison extends GeneralWorldHandler
{
	private Race spawnRace;
	
	protected final int DF4_OWNED_GODELITE = 12120;
	protected final int BN_BUFF_REWARD_HENCH = 20410;
	protected final int HP_SPRING_REGENERATION_L = 18408;
	protected final int HP_SPRING_REGENERATION_D = 18409;
	protected final int LF4_REWARD_NORTHERN_FORTRESS = 12103;
	protected final int LF4_REWARD_SOUTHERN_FORTRESS = 12106;
	protected final int LF4_TAKEN_NORTHERN_FORTRESS = 12115;
	protected final int LF4_TAKEN_SOUTHERN_FORTRESS = 12116;
	protected final int DF4_TAKEN_NORTHERN_FORTRESS = 12117;
	protected final int DF4_TAKEN_SOUTHERN_FORTRESS = 12118;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		Player winner = npc.getAggroList().getMostPlayerDamage();
        switch (npcId) {
			case 217095:
			case 217096:
			    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, winner.getObjectId(), npcId, 182206875, 1, true));
			break;
        }
    }
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///TEMPLE OF SCALES.
			case 257000:
				sendWorldBuff(Race.ELYOS, LF4_TAKEN_NORTHERN_FORTRESS, 1800000 * 1); //Kaisinel's Bane.
				sendWorldBuff(Race.ASMODIANS, LF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Dragon's Favor.
			break;
			case 257005:
				sendWorldBuff(Race.ELYOS, LF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Dragon's Favor.
				sendWorldBuff(Race.ASMODIANS, DF4_TAKEN_NORTHERN_FORTRESS, 1800000 * 1); //Marchutan's Bane.
			break;
			case 257010:
			    if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, LF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Dragon's Favor.
				} else {
					sendWorldBuff(Race.ASMODIANS, LF4_REWARD_NORTHERN_FORTRESS, 2700000 * 1); //Dragon's Favor.
				}
			break;
			///ALTAR OF AVARICE.
			case 257300:
				sendWorldBuff(Race.ELYOS, LF4_TAKEN_SOUTHERN_FORTRESS, 1800000 * 1); //Kaisinel's Boon.
				sendWorldBuff(Race.ASMODIANS, LF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Avarice's Favor.
			break;
			case 257305:
				sendWorldBuff(Race.ELYOS, LF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Avarice's Favor.
				sendWorldBuff(Race.ASMODIANS, DF4_TAKEN_SOUTHERN_FORTRESS, 1800000 * 1); //Marchutan's Boon.
			break;
			case 257310:
			    if (winner.getRace() == Race.ELYOS) {
					sendWorldBuff(Race.ELYOS, LF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Avarice's Favor.
				} else {
					sendWorldBuff(Race.ASMODIANS, LF4_REWARD_SOUTHERN_FORTRESS, 2700000 * 1); //Avarice's Favor.
				}
			break;
			///ENRAGED VEILLE.
			case 258200:
				despawnNpcs(getNpcs(296907)); //Veille's Aether Concentrator I.
		        despawnNpcs(getNpcs(296908)); //Veille's Aether Concentrator II.
		        despawnNpcs(getNpcs(296909)); //Veille's Aether Concentrator III.
				//The Agent's Adjutant has appeared.
				sendMsgByRace(1401218, Race.PC_ALL, 5000);
				//The Agent is here, and it is angry!
				sendMsgByRace(1401219, Race.PC_ALL, 10000);
				//Yushin <Veille's Eyes>
				spawn(210050000, 799094, 1079.0000f, 1492.0000f, 404.0000f, (byte) 90);
				sendWorldBuff(Race.ASMODIANS, DF4_OWNED_GODELITE, 3600000 * 1); //Mastarius's Energy.
				sendWorldBuff(Race.ASMODIANS, BN_BUFF_REWARD_HENCH, 7200000 * 1); //Victory Salute.
			break;
			///BOSS WINDSTREAM.
			case 215584: //Titan Starturtle.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(210050000, 281817, 338.0000f, 573.0000f, 458.0000f, (byte) 0, 755);
			break;
			case 216849: //Watcher Garma.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(210050000, 281817, 2602.0000f, 1526.0000f, 258.0000f, (byte) 0, 754);
			break;
			case 216848: //Illanthe Hundredyears.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(210050000, 281817, 1745.0000f, 1716.0000f, 226.0000f, (byte) 0, 1039);
			break;
			case 217071: //Esalki The Fourth.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(210050000, 281817, 2288.0000f, 1067.0000f, 285.0000f, (byte) 0, 2311);
			break;
			case 217072: //Huge Waterfall Starturtle.
				//A gust of air bursts forth.
				sendMsgByRace(1400486, Race.PC_ALL, 0);
				spawn(210050000, 281817, 1660.0000f, 928.0000f, 404.0000f, (byte) 0, 2292);
			break;
			///HUGE GRYPHU EGG.
			case 217095:
			    despawnNpc(npc);
			break;
			case 217096:
				spawn(210050000, 217097, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Lightwing Coiren.
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///ENRAGED VEILLE 2.x
			case 258200:
				switch (Rnd.get(1, 4)) {
					case 1:
					    //Kaisinel's Agent Veille has engaged in battle to defend Inggison.
						sendMsgByRace(1400529, Race.PC_ALL, 10000);
					break;
					case 2:
					    //Kaisinel's Agent Veille has engaged in battle to defend Inggison.
						sendMsgByRace(1400530, Race.PC_ALL, 10000);
					break;
					case 3:
					    //Kaisinel's Agent Veille has engaged in battle to defend Inggison.
						sendMsgByRace(1400531, Race.PC_ALL, 10000);
					break;
					case 4:
					    //Kaisinel's Agent Veille has engaged in battle to defend Inggison.
						sendMsgByRace(1400532, Race.PC_ALL, 10000);
					break;
				}
			break;
			case 282069: //Klaw Egg [Queen Klawnickt]
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(282069));
						spawn(210050000, 282070, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Supraklaw Shah.
					}
				}, 5000);
			break;
			case 799094: ///Yushin <Veille's Eyes>.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(799094));
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
			///https://aioncodex.com/3x/quest/10020/
			case 700600: //Enhanced Obelisk.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(700600));
					}
				}, 60000);
			break;
			///https://aioncodex.com/3x/quest/10025/
			case 700606: //Special Obelisk.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(700606));
					}
				}, 60000);
			break;
			///https://aioncodex.com/3x/quest/10025/
			case 700640: //Stonecrafted Obelisk.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(700640));
					}
				}, 60000);
			break;
			case 217097: //Lightwing Coiren.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(217097));
					}
				}, 180000);
			break;
			///ILLUSION GATE.
			case 296526:
				spawn(210050000, 296528, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(210050000, 296531, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(210050000, 296532, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296533:
				spawn(210050000, 296535, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(210050000, 296538, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(210050000, 296539, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 296540:
				spawn(210050000, 296542, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(210050000, 296544, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(210050000, 296545, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 216501: //Chief Kurnus.
				///Advent Of Chief Kurnus.
				sendMsgByRace(1401807, Race.PC_ALL, 0);
			break;
			case 216502: //Titch Kippie.
				///Advent Of Titch Kippie.
				sendMsgByRace(1401804, Race.PC_ALL, 0);
			break;
			case 216503: //Glugbeard.
				///Advent Of Glugbeard.
				sendMsgByRace(1401805, Race.PC_ALL, 0);
			break;
			case 216504: //Elder Narikiki.
				///Advent Of Elder Narikiki.
				sendMsgByRace(1401806, Race.PC_ALL, 0);
			break;
			case 216505: //Rockhorn.
				///Rockhorn.
				sendMsgByRace(1401808, Race.PC_ALL, 0);
			break;
			case 216507: //Skuma Silvereye.
				///Advent Of Skuma Silvereye.
				sendMsgByRace(1401811, Race.PC_ALL, 0);
			break;
			case 216510: //Skar.
				///Advent Of Skar.
				sendMsgByRace(1401812, Race.PC_ALL, 0);
			break;
			case 216512: //Priest Zitan.
				///Advent Of Priest Zitan.
				sendMsgByRace(1401813, Race.PC_ALL, 0);
			break;
			case 216513: //Tiritaphon.
				///Advent Of Tiritaphon.
				sendMsgByRace(1401809, Race.PC_ALL, 0);
			break;
			case 216514: //Queen Klawnickt.
				///Advent Of Queen Klawnickt.
				sendMsgByRace(1401814, Race.PC_ALL, 0);
			break;
			case 216515: //Mervin.
				///Advent Of Mervin.
				sendMsgByRace(1401810, Race.PC_ALL, 0);
			break;
			case 216516: //Omega.
				///Advent Of Omega.
				sendMsgByRace(1401800, Race.PC_ALL, 0);
			break;
			case 216517: //Kyang Redmane.
				///Advent Of Kyang Redmane.
				sendMsgByRace(1401815, Race.PC_ALL, 0);
			break;
			case 216518: //Latzio Stoneheart.
				///Advent Of Latzio Stoneheart.
				sendMsgByRace(1401816, Race.PC_ALL, 0);
			break;
			case 216519: //Captain Voltayre.
				///Advent Of Captain Voltayre.
				sendMsgByRace(1401817, Race.PC_ALL, 0);
			break;
			case 216521: //Baydeeafa.
				///Advent Of Baydeeafa.
				sendMsgByRace(1401818, Race.PC_ALL, 0);
			break;
			case 216522: //Sukana The Learned.
				///Advent Of Sukana The Learned.
				sendMsgByRace(1401819, Race.PC_ALL, 0);
			break;
			case 216523: //Head Priest Nashuma.
				///Advent Of Head Priest Nashuma.
				sendMsgByRace(1401801, Race.PC_ALL, 0);
			break;
			///TREASURE BOX.
			case 216595:
			case 216596:
			case 216597:
			case 216598:
			case 216599:
			case 216601:
			case 216788:
			case 217099:
			case 217101:
			case 217103:
			case 217104:
			case 217105:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void onDespawn(Npc npc) {
		switch (npc.getNpcId()) {
			///ENRAGED VEILLE 2.x
			case 258200:
				//Kaisinel's Agent Veille has disappeared.
				sendMsgByRace(1400319, Race.PC_ALL, 0);
    		break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			///HUGE GRYPHU EGG.
			case 206139:
			    if (MathUtil.isIn3dRange(npc, player, 8)) {
					despawnNpc(npc);
					respawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					switch (Rnd.get(1, 2)) {
						case 1:
							spawn(210050000, 217095, 911.1558f, 1582.8118f, 240.0000f, (byte) 58, 2333);
						break;
						case 2:
							spawn(210050000, 217096, 911.1558f, 1582.8118f, 240.0000f, (byte) 58, 2333);
						break;
					}
				}
			break;
			///VITALITY WELL.
			case 257198:
			case 257498:
			    if (MathUtil.isIn3dRange(npc, player, 8)) {
					if (!player.getEffectController().hasAbnormalEffect(HP_SPRING_REGENERATION_L)) {
						if (player.getRace() == Race.ELYOS) {
							SkillEngine.getInstance().applyEffectDirectly(HP_SPRING_REGENERATION_L, player, player, 20000 * 1); //Blessing Of Guardian Spring.
						}
					}
				}
			break;
			case 257199:
			case 257499:
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
			///PHANOE GATE.
			case 730296: //Phanoe Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						phanoeGateIn(player, 2513.0000f, 820.0000f, 415.0000f, (byte) 18);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730297: //Phanoe Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						phanoeGateOut(player, 2507.0000f, 812.0000f, 415.0000f, (byte) 78);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///DIMAIA GATE.
			case 730298: //Dimaia Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						dimaiaGateIn(player, 542.0000f, 714.0000f, 484.0000f, (byte) 17);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730299: //Dimaia Gate.
				if (dialogId == 10000) {
					if (player.getLevel() >= 50) {
						dimaiaGateOut(player, 536.0000f, 705.0000f, 484.0000f, (byte) 77);
					} else {
						///You cannot move to that destination.
					    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///FOUNTAIN REWARD.
			case 730241: //Inggison Coin Fountain.
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
			/// https://aioncodex.com/3x/quest/11036/
			case 700610: //Wet Parchment Book.
				despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182206731, 1);
			break;
			///https://aioncodex.com/3x/quest/11123/
			case 700616: //Unknow Book.
				despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182206798, 1);
			break;
			/// https://aioncodex.com/3x/quest/11046/
			case 700631: //Small Jewel Box.
				despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182206745, 1);
			break;
			///https://aioncodex.com/3x/quest/11143/
			case 700909: //Baby Basket.
				despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182206866, 1);
			break;
			case 700759: //Pluma Bait.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216608, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Lightningbeak Pabu.
			break;
			case 700760: //Spiritcaller Incense Burner.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216609, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Celodus Twistrings.
			break;
			case 700761: //Half-Rotten Animal Carcass.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216614, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Twister.
			break;
			case 700762: //Researcher's Equipment.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216615, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Valkinas The Scholar.
			break;
			case 700763: //Acid Canister.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216620, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Crimsonbark.
			break;
			case 700764: //Tricorn Food.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216621, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sweetsteppe.
			break;
			///SILENTERA CANYON.
			case 730256:
			    if (SiegeService.getInstance().isSiegeInProgress(2011) && SiegeService.getInstance().isSiegeInProgress(2021)) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 27));
				} else {
					underpassIn(player, 471.0000f, 460.0000f, 330.0000f, (byte) 0);
				}
			break;
			///TELEPORTATION TOWER [Temple Of Scales]
			case 257233:
			case 257234:
			    templeOfScales(player, 1733.0000f, 2215.0000f, 328.0000f, (byte) 96);
			break;
			///TELEPORTATION TOWER [Altar Of Avarice]
			case 257533:
			case 257534:
			    altarOfAvarice(player, 875.0000f, 1958.0000f, 340.0000f, (byte) 86);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void phanoeGateIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void phanoeGateOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void dimaiaGateIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void dimaiaGateOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void underpassIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600010000, 1, x, y, z, h);
	}
	protected void templeOfScales(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void altarOfAvarice(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("SOTERIA_SANCTUARY_210050000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("UNDIRBORG_OBSERVATION_POST_210050000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("INGGISON_ILLUSION_FORTRESS_210050000")) {
			switch (player.getRace()) {
				case ASMODIANS:
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