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

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import javolution.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300170000)
public class Beshmundir_Temple extends GeneralInstanceHandler
{
	private int debufflich;
	private int drakanmonk;
	private int IDCT_SpecterN_Spawn;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private final FastList<Future<?>> beshmundirTask = FastList.newInstance();
	
	protected final int IDCATACOMBS_SPECTRE_BUFF = 18895;
	protected final int NPC_STATDOWN_1 = 19046;
	protected final int NPC_STATDOWN_2 = 19047;
	protected final int NPC_STATDOWN_3 = 19048;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 216248: //idcatacombsn_deathnightnmd_55_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000092, 1, true));
		    break;
			case 216251: //idcatacombsn_drakankey1nmd_55_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000093, 1, true));
		    break;
			case 216252: //idcatacombsn_drakankey2nmd_55_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000094, 1, true));
		    break;
			case 216253: //idcatacombsn_drakankey3nmd_55_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000095, 1, true));
		    break;
			case 216254: //idcatacombsn_drakankey4nmd_55_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000096, 1, true));
		    break;
			case 216764: //idcatacombs_keychest_n.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000097, 1, true));
		    break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
        doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///Vehala The Cursed.
		switch (Rnd.get(1, 2)) {
			case 1:
				final Npc idcatacombsn_undrakanfinightnmd_55_ah1 = (Npc) spawn(216242, 1100.8206f, 21.70855f, 234.1083f, (byte) 0);
				idcatacombsn_undrakanfinightnmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_12");
				WalkManager.startWalking((NpcAI2) idcatacombsn_undrakanfinightnmd_55_ah1.getAi2());
			break;
			case 2:
				final Npc idcatacombsn_drakanfidaynmd_55_ah1 = (Npc) spawn(216243, 1100.8206f, 21.70855f, 234.1083f, (byte) 0);
				idcatacombsn_drakanfidaynmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_12");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakanfidaynmd_55_ah1.getAi2());
			break;
		}
		///Gatekeeper Darfall.
		switch (Rnd.get(1, 2)) {
			case 1:
				final Npc idcatacombsn_drakankey1nmd_55_ah1 = (Npc) spawn(216251, 1492.8317f, 1497.5365f, 300.3300f, (byte) 0);
				idcatacombsn_drakankey1nmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_23");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey1nmd_55_ah1.getAi2());
			break;
			case 2:
				final Npc idcatacombsn_drakankey1nmd_55_ah2 = (Npc) spawn(216251, 1499.1567f, 1528.0023f, 304.6597f, (byte) 0);
				idcatacombsn_drakankey1nmd_55_ah2.getSpawn().setWalkerId("idcatacombs_path_26");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey1nmd_55_ah2.getAi2());
			break;
		}
		///Gatekeeper Kutarrun.
		switch (Rnd.get(1, 2)) {
			case 1:
				final Npc idcatacombsn_drakankey2nmd_55_ah1 = (Npc) spawn(216252, 1395.1289f, 1464.8728f, 307.7928f, (byte) 0);
				idcatacombsn_drakankey2nmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_25");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey2nmd_55_ah1.getAi2());
			break;
			case 2:
				final Npc idcatacombsn_drakankey2nmd_55_ah2 = (Npc) spawn(216252, 1463.3372f, 1500.9946f, 301.3335f, (byte) 0);
				idcatacombsn_drakankey2nmd_55_ah2.getSpawn().setWalkerId("idcatacombs_path_21");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey2nmd_55_ah2.getAi2());
			break;
		}
		///Gatekeeper Samarrn.
		switch (Rnd.get(1, 2)) {
			case 1:
				final Npc idcatacombsn_drakankey3nmd_55_ah1 = (Npc) spawn(216253, 1505.1925f, 1354.1690f, 307.7928f, (byte) 0);
				idcatacombsn_drakankey3nmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_24");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey3nmd_55_ah1.getAi2());
			break;
			case 2:
				final Npc idcatacombsn_drakankey3nmd_55_ah2 = (Npc) spawn(216253, 1539.1180f, 1460.9127f, 300.3300f, (byte) 0);
				idcatacombsn_drakankey3nmd_55_ah2.getSpawn().setWalkerId("idcatacombs_path_22");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey3nmd_55_ah2.getAi2());
			break;
		}
		///Gatekeeper Rhapsharr.
		switch (Rnd.get(1, 2)) {
			case 1:
				final Npc idcatacombsn_drakankey4nmd_55_ah1 = (Npc) spawn(216254, 1551.0248f, 1433.5009f, 301.3335f, (byte) 0);
				idcatacombsn_drakankey4nmd_55_ah1.getSpawn().setWalkerId("idcatacombs_path_20");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey4nmd_55_ah1.getAi2());
			break;
			case 2:
				final Npc idcatacombsn_drakankey4nmd_55_ah2 = (Npc) spawn(216254, 1575.0342f, 1441.1349f, 304.6597f, (byte) 0);
				idcatacombsn_drakankey4nmd_55_ah2.getSpawn().setWalkerId("idcatacombs_path_27");
				WalkManager.startWalking((NpcAI2) idcatacombsn_drakankey4nmd_55_ah2.getAi2());
			break;
		}
		///Dorakiki's Chest.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(216690, 1047.0406f, 1032.8499f, 282.4128f, (byte) 0);
			break;
			case 2:
				spawn(216690, 1273.5209f, 1134.4819f, 282.1250f, (byte) 0);
			break;
		}
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 216245: ///Macunbello.
				SkillEngine.getInstance().applyEffectDirectly(NPC_STATDOWN_1, npc, npc, 0);
			break;
			case 799343: ///Tiamat's Stormer [Q20025 Quest For Siel's Relics]
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(799343));
					}
				}, 300000);
			break;
			case 799506: ///Faithful Respondent Utra [Q30208 The Truth Hurts] & [Q30308 Summon Respondent Utra]
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(799506));
					}
				}, 120000);
			break;
		}
	}
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			case 799517: //Plegeton Boatman [1st Island]
				if (dialogId == 10000) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(85001);
					PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, 85001, 0));
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new S_QUEST(0, 600));
							}
						}
					});
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 799518: //Plegeton Boatman [2nd Island]
				if (dialogId == 10000) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(86001);
					PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, 86001, 0));
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 799519: //Plegeton Boatman [3rd Island]
				if (dialogId == 10000) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(87001);
					PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, 87001, 0));
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 799520: //Plegeton Boatman [4th Island]
				if (dialogId == 10000) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(88001);
					PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, 88001, 0));
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	@Override
    public void handleUseItemFinish(final Player player, final Npc npc) {
        switch (npc.getNpcId()) {
			case 730274: //Altar Of Soul Invocation.
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs30208 = player.getQuestStateList().getQuestState(30208); //[Group] The Truth Hurts.
						if (qs30208 != null && qs30208.getStatus() == QuestStatus.START && qs30208.getQuestVarById(0) == 0 &&
						    player.getInventory().decreaseByItemId(182209610, 1)) { //Perfumed Oil.
							despawnNpc(npc);
							spawn(799506, player.getX(), player.getY(), player.getZ(), (byte) 0); //Faithful Respondent Utra.
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs30308 = player.getQuestStateList().getQuestState(30308); //[Group] Summon Respondent Utra.
						if (qs30308 != null && qs30308.getStatus() == QuestStatus.START && qs30308.getQuestVarById(0) == 0 &&
						    player.getInventory().decreaseByItemId(182209710, 1)) { //Perfumed Oil.
							despawnNpc(npc);
							spawn(799506, player.getX(), player.getY(), player.getZ(), (byte) 0); //Faithful Respondent Utra.
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
				}
            break;
			case 730275: //Rift Orb.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs30211 = player.getQuestStateList().getQuestState(30211); //The Rod And The Orb.
				    if (qs30211 != null && qs30211.getStatus() == QuestStatus.START && qs30211.getQuestVarById(0) == 0 &&
					    player.getInventory().decreaseByItemId(182209614, 1)) { //Quartz Rod.
						ClassChangeService.onUpdateQuest30211(player);
						PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 443));
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs30311 = player.getQuestStateList().getQuestState(30311); //The Rod And The Orb.
					if (qs30311 != null && qs30311.getStatus() == QuestStatus.START && qs30311.getQuestVarById(0) == 0 &&
					    player.getInventory().decreaseByItemId(182209714, 1)) { //Quartz Rod.
						ClassChangeService.onUpdateQuest30311(player);
						PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 443));
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
            break;
			case 730276: //Prison Of Ice Entrance.
				prisonOfIce(534.0000f, 1333.0000f, 223.0000f, (byte) 0);
            break;
			case 730290: //Entrance Of Blue Flame Incinerator.
				if (player.getInventory().decreaseByItemId(185000091, 1)) { //Incinerator Key.
					despawnNpc(npc);
					despawnNpcs(instance.getNpcs(206173));
				} else {
					///You cannot use it as the required quest has not been completed.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
				}
            break;
        }
    }
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206173: //Invincible Barriere.
				if (MathUtil.isIn3dRange(npc, player, 5)) {
					if (!player.isGM()) {
						moveBack(player, 1428.0000f, 1113.0000f, 286.0000f, (byte) 109);
					}
				}
			break;
		}
    }
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 216297: //Balaur Statue https://aioncodex.com/3x/quest/30244-30344
			    despawnNpc(npc);
				spawn(216295, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Monolithic Gladiator.
            break;
			case 216298: //Balaur Monument.
			    despawnNpc(npc);
				spawn(216296, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Monolithic Ambusher.
            break;
			case 216249: //Flarestorm.
				despawnNpcs(instance.getNpcs(730290));
            break;
			case 216238: //Captain Lakhara.
			    doors.get(470).setOpen(true);
            break;
			case 216739: //Warrior Monument.
                despawnNpc(npc);
				IDCT_SpecterN_Spawn++;
				if (IDCT_SpecterN_Spawn == 5) {
                	//The Warrior Monument has been destroyed. Ahbana the Wicked is on alert.
					sendMsgByRace(1400465, Race.PC_ALL, 0);
                } else if (IDCT_SpecterN_Spawn == 10) {
                	//Ahbana the Wicked has appeared in the Watcher's Nexus.
					sendMsgByRace(1400470, Race.PC_ALL, 5000);
					sp(216239, 1356.0000f, 149.0000f, 246.0000f, (byte) 29, 5000, 0, null); //Ahbana The Wicked.
                }
            break;
			case 216239: //Ahbana The Wicked.
			    doors.get(471).setOpen(true);
            break;
			case 216587: //Brutal Soulwatcher (1st Island)
				sp(799518, 933.0000f, 444.0000f, 222.0000f, (byte) 21, 3000, 0, null); //Plegeton Boatman II.
			break;
			case 216588: //Brutal Soulwatcher (2nd Island)
				sp(799519, 788.0000f, 442.0000f, 222.0000f, (byte) 0, 3000, 0, null); //Plegeton Boatman III.
			break;
			case 216589: //Brutal Soulwatcher (3th Island)
				sp(799520, 818.0000f, 277.0000f, 220.0000f, (byte) 53, 3000, 0, null); //Plegeton Boatman IV.
			break;
			case 216287: //Elyos Spiritblade.
			case 216288: //Elyos Spiritmage.
			case 216289: //Elyos Spiritbow.
			case 216290: //Elyos Spiritsalve.
			case 216291: //Asmodian Soulsword.
			case 216292: //Asmodian Soulspell.
			case 216293: //Asmodian Soulranger.
			case 216294: //Asmodian Soulmedic.
				final Npc macunbello2 = instance.getNpc(216245); //Macunbello.
				debufflich++;
				if (macunbello2 != null) {
				    if (debufflich == 12) {
					    //Macunbello's power is weakening.
					    sendMsgByRace(1400466, Race.PC_ALL, 0);
				    } else if (debufflich == 24) {
						macunbello2.getAi2().think();
						//Macunbello's power has weakened.
					    sendMsgByRace(1400467, Race.PC_ALL, 0);
						macunbello2.getEffectController().removeEffect(NPC_STATDOWN_1);
						SkillEngine.getInstance().applyEffectDirectly(NPC_STATDOWN_2, macunbello2, macunbello2, 0);
				    } else if (debufflich == 36) {
						macunbello2.getAi2().think();
					    //Macunbello has been crippled.
					    sendMsgByRace(1400468, Race.PC_ALL, 0);
						macunbello2.getEffectController().removeEffect(NPC_STATDOWN_2);
						SkillEngine.getInstance().applyEffectDirectly(NPC_STATDOWN_3, macunbello2, macunbello2, 0);
				    }
				}
			break;
			case 216590: //Temadaro.
				doors.get(467).setOpen(true);
			    instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							if (player.getRace() == Race.ELYOS) {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 445));
							} else {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 446));
							}
							PacketSendUtility.sendPacket(player, new S_QUEST(0, 0));
						}
					}
				});
			break;
			case 216246: //The Great Virhana.
				doors.get(473).setOpen(true);
			break;
			case 216250: //Dorakiki The Bold.
				despawnNpcs(instance.getNpcs(281647)); //Fixit.
				despawnNpcs(instance.getNpcs(281648)); //Sorcerer Haskin.
				despawnNpcs(instance.getNpcs(281649)); //Chopper.
				final Npc lupukin = (Npc) spawn(216527, 1164.5264f, 1210.8931f, 283.3387f, (byte) 107);
				///You saved me, Daeva, nyerk!
				NpcShoutsService.getInstance().sendMsg(lupukin, 1111357, lupukin.getObjectId(), 0, 0);
				///You saved my life. Thank you very much!
				NpcShoutsService.getInstance().sendMsg(lupukin, 342319, lupukin.getObjectId(), 0, 5000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 216248: //Taros Lifebane.
				final Npc tarosPureblood = (Npc) spawn(216528, 1384.0000f, 1299.0000f, 302.0000f, (byte) 107); //Taros Pureblood.
				///Finally! I'm free of the curse! I'll never forget your kindness.
				NpcShoutsService.getInstance().sendMsg(tarosPureblood, 1500075, tarosPureblood.getObjectId(), 0, 0);
				///You saved my life. Thank you very much!
				NpcShoutsService.getInstance().sendMsg(tarosPureblood, 342319, tarosPureblood.getObjectId(), 0, 5000);
			break;
			case 216256: //Protector Pahraza.
			case 216258: //Protector Dinata.
			case 216260: //Protector Narma.
			case 216262: //Judge Kramaka.
				drakanmonk++;
				if (drakanmonk == 4) {
                	spawn(216764, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0); //Mystery Box.
                }
			break;
			case 216263: //Isbariya The Resolute.
				//The Seal Protector has fallen. The Rift Orb shines while the seal weakens.
				sendMsgByRace(1400480, Race.PC_ALL, 3000);
				sp(216264, 556.0000f, 1367.0000f, 224.0000f, (byte) 75, 3000, 0, null); //Stormwing.
				sp(730276, 1598.0000f, 1603.0000f, 306.0000f, (byte) 99, 3000, 0, null); //Prison Of Ice Gate.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							if (player.getRace() == Race.ELYOS) {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 439));
							} else {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 440));
							}
						}
					}
				});
			break;
			case 216264: //Stormwing.
				sp(730287, 565.0000f, 1376.0000f, 224.0000f, (byte) 74, 3000, 0, null); //Rift Orb.
			break;
			case 281538: //Soulform.
			    despawnNpc(npc);
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDCATACOMBS_SPECTRE_BUFF, player, player, 15000 * 1);
				    }
			    });
			break;
		}
    }
	
	protected void prisonOfIce(float x, float y, float z, byte h) {
		for (Player player: instance.getPlayersInside()) {
			if (player.isOnline()) {
				TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
			}
		}
	}
	protected void moveBack(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000091, storage.getItemCountByItemId(185000091));
		storage.decreaseByItemId(185000092, storage.getItemCountByItemId(185000092));
		storage.decreaseByItemId(185000093, storage.getItemCountByItemId(185000093));
		storage.decreaseByItemId(185000094, storage.getItemCountByItemId(185000094));
		storage.decreaseByItemId(185000095, storage.getItemCountByItemId(185000095));
		storage.decreaseByItemId(185000096, storage.getItemCountByItemId(185000096));
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = beshmundirTask.head(), end = beshmundirTask.tail(); (n = n.getNext()) != end;) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        beshmundirTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    spawn(npcId, x, y, z, h, entityId);
                    if (msg > 0) {
                        sendMsgByRace(msg, race, 0);
                    }
                }
            }
        }, time));
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        beshmundirTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time));
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
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
		doors.clear();
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
}