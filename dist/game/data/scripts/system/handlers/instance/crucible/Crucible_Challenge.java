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
package instance.crucible;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.model.instance.playerreward.CruciblePlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANT_DUNGEON_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_WORLD_SCENE_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300320000)
public class Crucible_Challenge extends Crucible_System
{
	private int dieCount;
	private int spawnCount;
	private int stage1Count;
	private int rewardCount;
	private Future<?> bonusTimer;
	
	protected final int BN_CHARMED_IDNOVICE = 19226;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int itemId = 0;
		Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 217827: //Arminos' Treasure Chest.
			case 217828: //Arminos' Treasure Chest.
			case 217829: //Arminos' Treasure Chest.
				int count = 0;
				switch (rewardCount) {
					case 0:
						switch (Rnd.get(1, 3)) {
							case 1:
								count = 3;
							break;
							case 2:
								count = 10;
							break;
							case 3:
								count = 1;
							break;
						}
					break;
					case 1:
						switch (Rnd.get(1, 3)) {
							case 1:
								count = 5;
							break;
							case 2:
								count = 18;
							break;
							case 3:
								count = 1;
							break;
						}
					break;
					case 2:
						switch (Rnd.get(1, 3)) {
							case 1:
								count = 3;
							break;
							case 2:
								count = 26;
							break;
							case 3:
								count = 250;
							break;
						}
					break;
				} switch (npcId) {
					case 217827: //Arminos' Treasure Chest.
						despawnNpcs(instance.getNpcs(217828));
						despawnNpcs(instance.getNpcs(217829));
					break;
					case 217828: //Arminos' Treasure Chest.
						despawnNpcs(instance.getNpcs(217827));
						despawnNpcs(instance.getNpcs(217829));
					break;
					case 217829: //Arminos' Treasure Chest.
						despawnNpcs(instance.getNpcs(217827));
						despawnNpcs(instance.getNpcs(217828));
					break;
				} if (count == 0) {
					if (rewardCount == 0) {
						sp(217837, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 0); //Pet Kirca.
					} else if (rewardCount == 1) {
						sp(217838, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 0); //Pet Manduri.
					} else if (rewardCount == 1) {
						sp(217839, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 0); //Pet Starcrab.
					}
					despawnNpc(npc);
					return;
				}
				rewardCount++;
				Player player = null;
				if (!instance.getPlayersInside().isEmpty()) {
					player = instance.getPlayersInside().get(0);
				} if (player != null) {
					if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04B_300320000"))) {
						setEvent(StageType.PASS_STAGE_4, 0);
						sp(205667, 1258.8464f, 237.85518f, 405.39673f, (byte) 0, 0);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(4), 4000);
					} else if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04A_300320000"))) {
						setEvent(StageType.PASS_STAGE_4, 0);
						sp(205677, 1271.5472f, 791.36145f, 436.64017f, (byte) 0, 0);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(4), 4000);
					}
				}
				dropItems.clear();
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000169, count, true));
			break;
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		sp(205668, 345.0000f, 1662.0000f, 95.0000f, (byte) 0, 10000);
		sp(205682, 383.0000f, 1667.0000f, 97.0000f, (byte) 60, 10000);
		sp(217758, 380.0000f, 1670.0000f, 97.0000f, (byte) 78, 10000); //Worthiness Ticket.
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onEnterInstance(player);
		sendPacket(0, 0);
		sendEventPacket();
	}
	
	private void sendPacket(final int nameId, final int points) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(instanceReward));
					if (nameId != 0) {
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400237, new DescriptionId(nameId * 2 + 1), points));
					}
				}
			}
		});
	}
	
	private void sendEventPacket() {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new S_WORLD_SCENE_STATUS(2, stageType.getId(), stageType.getType()));
				}
			}
		});
	}
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 217786:
			case 217788:
			case 217789:
			case 217790:
				SkillEngine.getInstance().applyEffectDirectly(BN_CHARMED_IDNOVICE, npc, npc, 0);
			break;
			case 205668: ///Record Keeper.
			    final Npc recordKeeper1 = instance.getNpc(205668);
				///Quick lollygagging! Come over here if you want to start Stage 1.
				NpcShoutsService.getInstance().sendMsg(recordKeeper1, 1111470, recordKeeper1.getObjectId(), 0, 2000);
			break;
			case 205669: ///Record Keeper.
			    final Npc recordKeeper2 = instance.getNpc(205669);
				///Get ready! It's time for Stage 2 to start, nyerk!
				NpcShoutsService.getInstance().sendMsg(recordKeeper2, 1111471, recordKeeper2.getObjectId(), 0, 2000);
			break;
			case 205670: ///Record Keeper.
			    final Npc recordKeeper3 = instance.getNpc(205670);
				///Now, prepare for Stage 3, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper3, 1111472, recordKeeper3.getObjectId(), 0, 2000);
			break;
			case 205666: ///Record Keeper.
			    final Npc recordKeeper4A = instance.getNpc(205666);
				///Take a deep breath now... it's time for Stage 4 to begin.
				NpcShoutsService.getInstance().sendMsg(recordKeeper4A, 1111473, recordKeeper4A.getObjectId(), 0, 2000);
			break;
			case 205671: ///Record Keeper.
			    final Npc recordKeeper4B = instance.getNpc(205671);
				///Take a deep breath now... it's time for Stage 4 to begin.
				NpcShoutsService.getInstance().sendMsg(recordKeeper4B, 1111473, recordKeeper4B.getObjectId(), 0, 2000);
			break;
			case 205672: ///Record Keeper.
			    final Npc recordKeeper5 = instance.getNpc(205672);
				///When you've recovered your composure, let me know and you can move on to Stage 5, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper5, 1111474, recordKeeper5.getObjectId(), 0, 2000);
			break;
			case 205673: ///Record Keeper.
			    final Npc recordKeeper6 = instance.getNpc(205673);
				///Are you ready? Shall I start up Stage 6?
				NpcShoutsService.getInstance().sendMsg(recordKeeper6, 1111475, recordKeeper6.getObjectId(), 0, 2000);
			break;
			case 205674: ///Record Keeper.
			    final Npc recordKeeper7 = instance.getNpc(205674);
				///You have completed Stage 1, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper7, 1111476, recordKeeper7.getObjectId(), 0, 2000);
			break;
			case 205675: ///Record Keeper.
			    final Npc recordKeeper8 = instance.getNpc(205675);
				///You have completed Stage 2, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper8, 1111477, recordKeeper8.getObjectId(), 0, 2000);
			break;
			case 205676: ///Record Keeper.
			    final Npc recordKeeper9 = instance.getNpc(205676);
				///You have completed Stage 3, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper9, 1111478, recordKeeper9.getObjectId(), 0, 2000);
			break;
			case 205667: ///Record Keeper.
			    final Npc recordKeeper10A = instance.getNpc(205667);
				///You have completed Stage 4, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper10A, 1111479, recordKeeper10A.getObjectId(), 0, 2000);
			break;
			case 205677: ///Record Keeper.
			    final Npc recordKeeper10B = instance.getNpc(205677);
				///You have completed Stage 4, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper10B, 1111479, recordKeeper10B.getObjectId(), 0, 2000);
			break;
			case 205678: ///Record Keeper.
			    final Npc recordKeeper11 = instance.getNpc(205678);
				///Congratulations. You have passed Stage 5!
				NpcShoutsService.getInstance().sendMsg(recordKeeper11, 1111480, recordKeeper11.getObjectId(), 0, 2000);
			break;
			case 205679: ///Record Keeper.
			    final Npc recordKeeper12 = instance.getNpc(205679);
				///Congratulations! You have completed Stage 6, nyerk!
				NpcShoutsService.getInstance().sendMsg(recordKeeper12, 1111481, recordKeeper12.getObjectId(), 0, 2000);
			break;
			case 730459: ///Crucible Rift.
				for (Player player: instance.getPlayersInside()) {
					if (player.isOnline()) {
						///A Crucible Rift has appeared at the spot where Vanktrist vanished. I'd better go investigate!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111482, player.getObjectId(), 2));
					}
				}
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						for (Player player: instance.getPlayersInside()) {
							if (player.isOnline()) {
								///Hmm, just as I suspected... Tiamat's Balaur have infiltrated the Crucible.
								PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111483, player.getObjectId(), 2));
							}
						}
					}
				}, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						for (Player player: instance.getPlayersInside()) {
							if (player.isOnline()) {
								///Weird. It looks like a Crucible... just not OUR Crucible. I wonder who it belongs to?
								PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111484, player.getObjectId(), 2));
							}
						}
					}
				}, 8000);
			break;
			case 217804: ///Dimensional Vortex.
			    spawn(282374, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 730460: ///Crucible Rift.
				despawnNpc(npc);
				sp(205679, 1765.522f, 1282.1051f, 389.11743f, (byte) 0, 2000);
				teleportNewStage(player, 1759.0000f, 1273.0000f, 389.0000f, (byte) 10);
			break;
		}
	}
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			case 217820: ///Administrator Arminos.
				if (dialogId == 10000) {
					despawnNpc(npc);
					if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04B_300320000"))) {
						spawn(217827, 1250.1598f, 237.97736f, 405.3968f, (byte) 0);
						spawn(217828, 1250.1598f, 239.97736f, 405.3968f, (byte) 0);
						spawn(217829, 1250.1598f, 235.97736f, 405.3968f, (byte) 0);
					} else if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04A_300320000"))) {
						spawn(217827, 1265.9661f, 793.5348f, 436.64008f, (byte) 0);
						spawn(217828, 1265.9661f, 789.5348f, 436.6402f, (byte) 0);
						spawn(217829, 1265.9661f, 791.5348f, 436.64014f, (byte) 0);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///[BONUS RIFT]
			case 730459: ///Crucible Rift.
				if (dialogId == 10000) {
					despawnNpc(npc);
					switch (player.getRace()) {
						case ELYOS:
							sp(218200, 1765.4385f, 315.67407f, 469.25f, (byte) 114, 2000); //Rank 5, Asmodian Soldier Mediatec.
						break;
						case ASMODIANS:
							sp(218192, 1765.4385f, 315.67407f, 469.25f, (byte) 114, 2000); //Rank 5, Elyos Soldier Odos.
						break;
					}
					teleportNewStage(player, 1807.0000f, 306.0000f, 469.0000f, (byte) 54);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///[RECORD KEEPER]
			case 205668: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_1_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205674: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					sp(205683, 1821.5643f, 311.92484f, 469.4562f, (byte) 60, 2000);
					sp(205669, 1784.4633f, 306.98645f, 469.2500f, (byte) 0, 2000);
					teleportNewStage(player, 1796.0000f, 306.0000f, 469.0000f, (byte) 60);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205669: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_2_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205675: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					sp(205684, 1358.4021f, 1758.744f, 319.1873f, (byte) 70, 2000);
					sp(205670, 1307.5472f, 1732.9865f, 316.0777f, (byte) 6, 2000);
					teleportNewStage(player, 1324.0000f, 1738.0000f, 316.0000f, (byte) 70);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205670: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_3_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205676: ///Record Keeper.
			    if (dialogId == 10000) {
                    despawnNpc(npc);
					switch (Rnd.get(1, 2)) {
						case 1:
							sp(205663, 1295.7217f, 242.15009f, 406.03677f, (byte) 60, 2000);
							sp(205666, 1258.7214f, 237.85518f, 405.3968f, (byte) 0, 2000);
							teleportNewStage(player, 1270.0000f, 237.0000f, 405.0000f, (byte) 60);
						break;
						case 2:
							sp(205685, 1308.9664f, 796.20276f, 437.29678f, (byte) 60, 2000);
							sp(205671, 1271.4222f, 791.36145f, 436.64017f, (byte) 0, 2000);
							teleportNewStage(player, 1283.0000f, 791.0000f, 436.6403f, (byte) 60);
						break;
					}
				}
			break;
			case 205666: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_KROMEDE_STAGE_4_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205671: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_HARAMEL_STAGE_4_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205667: ///Record Keeper.
			case 205677: ///Record Keeper.
			    if (dialogId == 10000) {
                    despawnNpc(npc);
					sp(205686, 383.30933f, 354.07846f, 96.07846f, (byte) 60, 2000);
					sp(205672, 346.52298f, 349.25586f, 96.0098f, (byte) 0, 2000);
					teleportNewStage(player, 357.0000f, 349.0000f, 96.0000f, (byte) 60);
				}
			break;
			case 205672: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_5_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205678: ///Record Keeper.
			    if (dialogId == 10000) {
                    despawnNpc(npc);
					sp(205687, 1747.3901f, 1250.201f, 389.11765f, (byte) 16, 2000);
					sp(205673, 1767.1036f, 1288.4425f, 389.11728f, (byte) 76, 2000);
					teleportNewStage(player, 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
				}
			break;
			case 205673: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_6_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205679: ///Record Keeper.
				if (dialogId == 10000) {
					onStopTraining(player);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///[ARBITER]
			case 205682: ///Arbiter.
				if (dialogId == 10000) { ///Worthiness Ticket.
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage1(player, 356.0000f, 1662.0000f, 95.0000f, (byte) 61);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205683: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage2(player, 1796.0000f, 306.0000f, 469.0000f, (byte) 60);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205684: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage3(player, 1324.0000f, 1738.0000f, 316.0000f, (byte) 70);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205663: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage41(player, 1270.0000f, 237.0000f, 405.0000f, (byte) 60);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205686: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage42(player, 357.0000f, 349.0000f, 96.0000f, (byte) 60);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205687: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage5(player, 1759.0000f, 1273.0000f, 389.0000f, (byte) 10);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205685: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) {
					    crucibleStage6(player, 1283.0000f, 791.0000f, 436.0000f, (byte) 60);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	protected void crucibleStage1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage41(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage42(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage5(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage6(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	@Override
	public void onDie(final Npc npc) {
		int score = 0;
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
            //STAGE 1
			case 217784:
			case 217785:
                score += 120;
            break;
			case 217783:
                score += 1600;
            break;
			//STAGE 2
			case 217797:
			case 217798:
			case 217799:
                score += 1100;
            break;
			case 217800:
			case 217801:
                score += 1650;
            break;
			case 217843:
			case 217845:
                score += 1400;
            break;
			case 217847:
                score += 4200;
            break;
			//STAGE 4
			case 217786:
			case 217787:
                score += 200;
            break;
			case 217788:
			case 217789:
			case 217790:
			case 217791:
			case 217792:
			case 217793:
                score += 1300;
            break;
			case 217794:
			case 217795:
                score += 5000;
            break;
			//STAGE 5
			case 217807:
			case 217808:
			case 217809:
			case 217810:
			case 217811:
			case 217812:
			case 217813:
			case 217814:
                score += 2500;
            break;
			case 217806:
			case 217815:
			case 217818:
			case 218562:
			case 218564:
			case 218565:
                score += 5800;
            break;
			//STAGE 6
            case 217819:
                score += 7200;
            break;
		} if (score != 0) {
			getPlayerReward(instance.getSoloPlayerObj()).addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
		int npcId = 0;
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 217785:
			case 217784:
				npcId = npc.getNpcId();
				stage1Count++;
				if (stage1Count == 2) {
					sp(npcId, 342.855f, 1652.6703f, 95.63069f, (byte) 0, 1000);
					sp(npcId, 342.83942f, 1673.2863f, 95.66078f, (byte) 0, 2000);
				}
				despawnNpc(npc);
				if (getNpcs(npcId).isEmpty()) {
					setEvent(StageType.START_STAGE_1_ROUND_2, 2000);
					sp(217783, 337.82263f, 1662.9073f, 95.27217f, (byte) 0, 2000);
				}
			break;
			case 217783:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217796)); //Grissil's Branch.
				setEvent(StageType.PASS_STAGE_1, 0);
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(1), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(1), 4000);
				sp(217758, 347.24026f, 1660.2524f, 95.35922f, (byte) 0, 0); //Worthiness Ticket.
				sp(205674, 345.52954f, 1662.6697f, 95.25f, (byte) 0, 0);
			break;
			case 217843:
			case 217845:
				despawnNpc(npc);
				sp(217848, 1309.2858f, 1732.6802f, 316.0825f, (byte) 0, 1000);
				sp(217848, 1306.1871f, 1731.7861f, 316.25168f, (byte) 0, 1000);
				sp(217848, 1308.4879f, 1734.4999f, 316f, (byte) 0, 1000);
				sp(217848, 1308.67419f, 1736.2063f, 316f, (byte) 0, 1000);
				sp(217848, 1306.9414f, 1736.5365f, 316f, (byte) 0, 1000);
				sp(217848, 1305.2534f, 1735.1603f, 315.94586f, (byte) 0, 1000);
				setEvent(StageType.START_STAGE_3_ROUND_2, 2000);
				sp(217847, 1307.2786f, 1734.3274f, 316f, (byte) 0, 2000);
				///Stop Gomju from perpetrating a senseless massacre!
				sendMsgByRace(1401086, Race.PC_ALL, 3000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(217848));
					}
				}, 20000);
			break;
			case 217847:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217846)); //Gomju's Minion.
				setEvent(StageType.PASS_STAGE_3, 0);
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(3), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(3), 4000);
				sp(205676, 1307.6722f, 1732.9865f, 316.07373f, (byte) 6, 0);
			break;
			case 217788:
			case 217789:
			case 217790:
				despawnNpc(npc);
				despawnNpcs(getNpcs(217786)); //Dukaki Guard.
				setEvent(StageType.START_STAGE_4_ROUND_2, 2000);
				sp(217794, 1271f, 791.5752f, 436.63998f, (byte) 0, 2000);
			break;
			case 217791:
			case 217792:
			case 217793:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217115)); //Watchdog Tog.
				despawnNpcs(instance.getNpcs(217805)); //Lady Angerr Bloodwing.
				despawnNpcs(instance.getNpcs(217787)); //Kaliga's Servant.
				setEvent(StageType.START_STAGE_4_ROUND_2, 2000);
				sp(217795, 1258.8425f, 237.91522f, 405.3968f, (byte) 0, 2000);
			break;
			case 217794:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282041)); //Brainwashed Fighter.
				despawnNpcs(instance.getNpcs(282042)); //Brainwashed Mumu Fighter.
				sp(217820, 1266.9661f, 791.5348f, 436.64014f, (byte) 0, 2000);
				setEvent(StageType.START_BONUS_STAGE_4, 2000);
			break;
			case 217795:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217111)); //Kaliga Bloodwing.
				sp(217820, 1251.1598f, 237.97736f, 405.3968f, (byte) 0, 2000);
				setEvent(StageType.START_BONUS_STAGE_4, 2000);
			break;
			case 217786:
			case 217787:
				despawnNpc(npc);
			break;
			case 217807:
			case 217808:
			case 217809:
			case 217810:
			case 217811:
			case 217812:
			case 217813:
			case 217814:
				despawnNpc(npc);
				setEvent(StageType.START_STAGE_5_ROUND_2, 2000);
				switch (Rnd.get(1, 6)) {
					case 1:
						npcId = 217806;
					break;
					case 2:
						npcId = 217815;
					break;
					case 3:
						npcId = 217818;
					break;
					case 4:
						npcId = 218562;
					break;
					case 5:
						npcId = 218564;
					break;
					case 6:
						npcId = 218565;
					break;
				}
				sp(npcId, 332.3786f, 349.31204f, 96.090935f, (byte) 0, 2000);
			break;
			case 217806:
			case 217815:
			case 217818:
			case 218562:
			case 218564:
			case 218565:
				despawnNpc(npc);
				setEvent(StageType.PASS_STAGE_5, 0);
				sp(205678, 346.64798f, 349.25586f, 96.090965f, (byte) 0, 0);
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(5), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(5), 4000);
			break;
			case 217819:
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						Player player = null;
						if (!instance.getPlayersInside().isEmpty()) {
							player = instance.getPlayersInside().get(0);
						}
						despawnNpc(npc);
						despawnNpcs(instance.getNpcs(217804)); //Dimensional Vortex.
						if (player != null) {
							final QuestState qs = player.getQuestStateList().getQuestState(player.getRace() == Race.ASMODIANS ? 28208 : 18208);
							if (qs != null && qs.getStatus() == QuestStatus.START) {
								if (qs.getQuestVarById(0) == 1 || qs.getQuestVarById(1) == 4) { //Kill x5 Vanktrist Spacetwine.
									sp(730459, 1765.7104f, 1281.2388f, 389.11743f, (byte) 0, 2000);
									return;
								}
							}
						}
						setEvent(StageType.PASS_STAGE_6, 0);
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(6), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(6), 4000);
						sp(205679, 1765.522f, 1282.1051f, 389.11743f, (byte) 0, 2000);
					}
				}, 4000);
			break;
			case 217837:
			case 217838:
				if (player != null) {
					if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04B_300320000"))) {
						sp(217820, 1251.1598f, 237.97736f, 405.3968f, (byte) 0, 0);
					} else if (player.isInsideZone(ZoneName.get("TRAINING_ROOM_04A_300320000"))) {
						sp(217820, 1266.9661f, 791.5348f, 436.64014f, (byte) 0, 0);
					}
				}
				despawnNpc(npc);
			break;
			case 217800: //Shoruken.
			case 217801: //Attatuken.
				despawnNpc(npc);
				if (getNpcs(217800).isEmpty() &&
				    getNpcs(217801).isEmpty()) {
					startBonusStage2();
				}
			break;
			case 217802:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217803));
				///Poppy was captured by the Dukaki Cooks... and roasted whole!
				sendMsgByRace(1401075, Race.PC_ALL, 0);
				sp(218570, 1780.5371f, 307.3513f, 469.25f, (byte) 0, 0); //Whole Roast Poppy.
				bonusTimer.cancel(false);
				passStage2();
			break;
			case 217797:
			case 217798:
			case 217799:
				despawnNpc(npc);
				if (getNpcs(217797).isEmpty() &&
				    getNpcs(217798).isEmpty() &&
					getNpcs(217799).isEmpty()) {
					startBonusStage2();
				}
			break;
			case 217803:
				despawnNpc(npc);
				dieCount++;
				if (dieCount == 5) {
					///There are 5 Dukaki Cooks remaining.
					sendMsgByRace(1401068, Race.PC_ALL, 0);
					///Careful! Poppy's health is very low.
					sendMsgByRace(1401069, Race.PC_ALL, 2000);
					///Poppy has almost reached the refuge. Just a little bit further!
					sendMsgByRace(1401070, Race.PC_ALL, 4000);
				} if (bonusTimer.isCancelled() && getNpcs(217803).isEmpty()) {
					passStage2();
					final Npc poppy = instance.getNpc(217802);
					if (poppy != null) {
						///Poppy has reached the refuge safely. A successful rescue!
						sendMsgByRace(1401071, Race.PC_ALL, 0);
						///You have eliminated all of the Dukaki Cooks and successfully rescued Poppy!
						sendMsgByRace(1401072, Race.PC_ALL, 3000);
						sp(218571, poppy.getX(), poppy.getY(), poppy.getZ(), (byte) 0, 0); //Poppy's Present.
						///Oink oink! Thank you!
						NpcShoutsService.getInstance().sendMsg(poppy, 342397, poppy.getObjectId(), 0, 0);
						poppy.getController().onDelete();
					}
				}
			break;
			case 217844:
			case 217842:
				final int fnpcId = npc.getNpcId();
				despawnNpc(npc);
				if (getNpcs(fnpcId).size() == 2) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							startStage3Round1_1(fnpcId);
						}
					}, 5000);
				}
			break;
			case 218192: //Rank 5, Elyos Soldier Odos.
				despawnNpc(npc);
				sp(217833, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2000); //Box Bonus.
				sp(730460, 1804.0000f, 309.0000f, 469.0000f, (byte) 63, 2000); //Crucible Rift Exit.
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(6), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(6), 4000);
            break;
			case 218200: //Rank 5, Asmodian Soldier Mediatec.
				despawnNpc(npc);
				sp(217833, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2000); //Box Bonus.
				sp(730460, 1804.0000f, 309.0000f, 469.0000f, (byte) 63, 2000); //Crucible Rift Exit.
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(6), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(6), 4000);
            break;
		}
	}
	
	private void startStage3Round1_1(int npcId) {
		int bossId = 0;
		switch (npcId) {
			case 217844:
				bossId = 217845;
			break;
			case 217842:
				bossId = 217843;
			break;
		}
		sp(bossId, 1287.6239f, 1724.2721f, 317.1485f, (byte) 6, 2000);
		despawnNpcs(instance.getNpcs(npcId));
		despawnNpcs(instance.getNpcs(217840));
		despawnNpcs(instance.getNpcs(218560));
		despawnNpcs(instance.getNpcs(218561));
		despawnNpcs(instance.getNpcs(217841));
	}
	
	@Override
	public void doReward(Player player) {
		CruciblePlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			if (playerReward.getPoints() != 0) {
				switch (Rnd.get(1, 3)) {
					case 1:
						playerReward.setIDArenaSoloReward01(1);
						playerReward.setLesserSupplementEternal(50);
						ItemService.addItem(player, 188052934, 1); //제2 템페르 소모품 꾸러미.
						ItemService.addItem(player, 166100015, 50); //[Supply] Lesser Supplement (Eternal)
					break;
					case 2:
						playerReward.setIDArenaSoloReward02(1);
						playerReward.setLesserSupplementEternal(25);
						ItemService.addItem(player, 188052935, 1); //제2 템페르 공훈 꾸러미.
						ItemService.addItem(player, 166100015, 25); //[Supply] Lesser Supplement (Eternal)
					break;
					case 3:
						playerReward.setIDArenaSoloLucky(1);
						playerReward.setLesserSupplementEternal(12);
						ItemService.addItem(player, 188052937, 1); //제2 템페르 행운의 확률 꾸러미.
						ItemService.addItem(player, 166100015, 12); //[Supply] Lesser Supplement (Eternal)
					break;
				}
				instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			}
		} else {
			TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
		}
		PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(instanceReward));
	}
	
	private void startBonusStage2() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				setEvent(StageType.START_BONUS_STAGE_2, 2000);
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///Poppy is running from the Dukaki Cooks. Eliminate them and help Poppy to reach the refuge.
				sendMsgByRace(1401067, Race.PC_ALL, 0);
				final Npc idarena_solo_s4_polymorph_porguss = (Npc) spawn(217802, 1780.5371f, 307.3513f, 469.2500f, (byte) 0);
				idarena_solo_s4_polymorph_porguss.getSpawn().setWalkerId("idarena_solo_s4_polymorph_porguss");
				WalkManager.startWalking((NpcAI2) idarena_solo_s4_polymorph_porguss.getAi2());
			}
		}, 1000);
		bonusTimer = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				spawnCount++;
				spawnBonus2();
				if (spawnCount == 10) {
					bonusTimer.cancel(false);
				}
			}
		}, 12000, 4000);
	}
	
	private void spawnBonus2() {
		switch (spawnCount) {
			case 1:
				sp(217803, 1789.923f, 310.853f, 469.25f, (byte) 0, 0);
			break;
			case 2:
				sp(217803, 1780.9126f, 315.63382f, 469.25f, (byte) 0, 0);
			break;
			case 3:
				///The Dukaki Cooks attacked and wounded Poppy!
				sendMsgByRace(1401082, Race.PC_ALL, 0);
				sp(217803, 1784.4752f, 300.1912f, 469.25f, (byte) 0, 0);
			break;
			case 4:
				sp(217803, 1793.2847f, 312.59842f, 469.25f, (byte) 0, 0);
			break;
			case 5:
				sp(217803, 1777.6609f, 316.1443f, 469.25f, (byte) 0, 0);
			break;
			case 6:
				sp(217803, 1776.5625f, 299.30347f, 469.25f, (byte) 0, 0);
			break;
			case 7:
				sp(217803, 1793.6469f, 301.40973f, 469.25f, (byte) 0, 0);
			break;
			case 8:
				///Poppy was attacked by the Dukaki Cooks. They're planning to roast Poppy for dinner!
				sendMsgByRace(1401083, Race.PC_ALL, 0);
				sp(217803, 1797.4891f, 310.50418f, 469.25f, (byte) 0, 0);
			break;
			case 9:
				sp(217803, 1782.552f, 319.43973f, 469.25f, (byte) 0, 0);
			break;
			case 10:
				sp(217803, 1776.1577f, 305.80396f, 469.25f, (byte) 0, 0);
			break;
		}
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
		if (bonusTimer != null) {
			bonusTimer.cancel(false);
		}
	}
	
	private void passStage2() {
		setEvent(StageType.PASS_STAGE_2, 0);
		sp(205675, 1784.5883f, 306.98645f, 469.25f, (byte) 0, 0);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				///You have eliminated all enemies in Round %0.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(2), 2000);
				///You have passed Stage %0!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(2), 4000);
			}
		});
	}
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		super.onDie(player, lastAttacker);
		int place = 0;
		if (isInZone(ZoneName.get("TRAINING_ROOM_01_300320000"), player)) {
			place = 1;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_02_300320000"), player)) {
			place = 2;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_03_300320000"), player)) {
			place = 3;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_04B_300320000"), player)) {
			place = 4;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_04A_300320000"), player)) {
			place = 5;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_05_300320000"), player)) {
			place = 6;
		} else if (isInZone(ZoneName.get("TRAINING_ROOM_06_300320000"), player)) {
			place = 7;
		}
		getPlayerReward(player.getObjectId()).setSpawnPosition(place);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                onReviveEvent(player);
            }
        }, 13000);
		return true;
	}
	
	protected void teleport(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void teleportNewStage(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		super.onReviveEvent(player);
		///You failed the training and have been sent to the Ready Room.
		sendMsgByRace(1400932, Race.PC_ALL, 0);
		switch (getPlayerReward(player.getObjectId()).getSpawnPosition()) {
			case 1:
				teleport(player, 356.0000f, 1662.0000f, 95.0000f, (byte) 61);
			break;
			case 2:
				teleport(player, 1819.0000f, 304.0000f, 469.0000f, (byte) 0);
			break;
			case 3:
				teleport(player, 1354.0000f, 1748.0000f, 318.0000f, (byte) 70);
			break;
			case 4:
				teleport(player, 1294.0000f, 234.0000f, 406.0000f, (byte) 0);
			break;
			case 5:
				teleport(player, 1307.0000f, 790.0000f, 437.0000f, (byte) 0);
			break;
			case 6:
				teleport(player, 381.0000f, 346.0000f, 96.0000f, (byte) 0);
			break;
			case 7:
				teleport(player, 1750.0000f, 1253.0000f, 389.0000f, (byte) 10);
			break;
		}
		return true;
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
        storage.decreaseByItemId(186000124, storage.getItemCountByItemId(186000124));
		storage.decreaseByItemId(186000125, storage.getItemCountByItemId(186000125));
		storage.decreaseByItemId(186000134, storage.getItemCountByItemId(186000134));
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onExitInstance(Player player) {
		removeItems(player);
		InstanceService.destroyInstance(player.getPosition().getWorldMapInstance());
	}
	
	@Override
	public void onPlayerLogin(Player player) {
		sendPacket(0, 0);
		sendEventPacket();
		if (getPlayerReward(player.getObjectId()).isRewarded()) {
			doReward(player);
		}
	}
	
	@Override
	public void onStopTraining(Player player) {
		doReward(player);
	}
	
	@Override
	public void onChangeStage(StageType type) {
		setEvent(type, 2000);
		int npcId = 0, barrelId = 0;
		Player player = null;
		if (!instance.getPlayersInside().isEmpty()) {
			player = instance.getPlayersInside().get(0);
		} switch (stageType) {
			case START_STAGE_1_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(1), 2000);
				switch (Rnd.get(1, 2)) {
					case 1:
						npcId = 217784;
					break;
					case 2:
						npcId = 217785;
					break;
				}
				sp(npcId, 334.85098f, 1657.8495f, 95.77262f, (byte) 0, 2000);
				sp(npcId, 334.74506f, 1668.7478f, 95.67427f, (byte) 0, 3000);
				sp(npcId, 350.63846f, 1663.84f, 95.385f, (byte) 0, 4000);
			break;
			case START_STAGE_2_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(2), 2000);
				switch (Rnd.get(1, 2)) {
					case 1:
						sp(217800, 1779.9993f, 305.76697f, 469.25f, (byte) 30, 2000);
						sp(217801, 1779.9845f, 308.82013f, 469.25f, (byte) 90, 2000);
					break;
					case 2:
						sp(217797, 1771.5549f, 307.09192f, 469.25f, (byte) 30, 2000);
						sp(217798, 1775.9717f, 317.69516f, 469.25f, (byte) 90, 3000);
						sp(217799, 1775.9646f, 296.3559f, 469.25f, (byte) 90, 4000);
					break;
				}
			break;
			case START_STAGE_3_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(3), 2000);
				switch (Rnd.get(1, 2)) {
					case 1:
						///Smash the Meat Barrel to lure and destroy the Starved Karnifs.
						sendMsgByRace(1401084, Race.PC_ALL, 0);
						npcId = 217842;
						barrelId = 217840;
					break;
					case 2:
						///Smash the Aether Barrel to lure and destroy the Thirsty Spirits.
						sendMsgByRace(1401085, Race.PC_ALL, 0);
						npcId = 217844;
						barrelId = 218560;
					break;
				}
				sp(barrelId, 1302.4689f, 1732.1583f, 316.4486f, (byte) 0, 2000);
				sp(npcId, 1288.6692f, 1730.5212f, 316.85333f, (byte) 0, 2000);
				sp(npcId, 1296.7896f, 1726.1091f, 316.82837f, (byte) 0, 2000);
				sp(npcId, 1294.3018f, 1730.9f, 316.875f, (byte) 0, 2000);
				sp(npcId, 1292.1957f, 1726.921f, 316.875f, (byte) 0, 2000);
				sp(npcId, 1293.0376f, 1722.3871f, 316.93515f, (byte) 0, 2000);
			break;
			case START_HARAMEL_STAGE_4_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(4), 2000);
				switch (Rnd.get(1, 3)) {
					case 1:
						npcId = 217788;
					break;
					case 2:
						npcId = 217789;
					break;
					case 3:
						npcId = 217790;
					break;
				}
				sp(217786, 1263.4213f, 791.8533f, 436.64014f, (byte) 60, 2000);
				sp(217786, 1267.2097f, 804.04456f, 436.64008f, (byte) 60, 3000);
				sp(217786, 1267.0653f, 781.0253f, 436.64017f, (byte) 60, 4000);
				sp(npcId, 1253.5984f, 791.6149f, 436.64008f, (byte) 0, 8000);
			break;
			case START_KROMEDE_STAGE_4_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(4), 2000);
				switch (Rnd.get(1, 3)) {
					case 1:
						npcId = 217791;
					break;
					case 2:
						npcId = 217792;
					break;
					case 3:
						npcId = 217793;
					break;
				}
				sp(217787, 1252.525f, 248.50781f, 405.38016f, (byte) 60, 2000);
				sp(217787, 1250.0901f, 237.69656f, 405.39676f, (byte) 60, 3000);
				sp(217787, 1253.0117f, 225.77977f, 405.3801f, (byte) 60, 4000);
				sp(npcId, 1242.3618f, 237.89081f, 405.3801f, (byte) 0, 8000);
			break;
			case START_STAGE_5_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(5), 2000);
				switch (Rnd.get(1, 8)) {
					case 1:
						npcId = 217807;
					break;
					case 2:
						npcId = 217808;
					break;
					case 3:
						npcId = 217809;
					break;
					case 4:
						npcId = 217810;
					break;
					case 5:
						npcId = 217811;
					break;
					case 6:
						npcId = 217812;
					break;
					case 7:
						npcId = 217813;
					break;
					case 8:
						npcId = 217814;
					break;
				}
				sp(npcId, 335.7365f, 337.93097f, 96.0909f, (byte) 0, 2000);
			break;
			case START_STAGE_6_ROUND_1:
				///Round %0 begins!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(6), 2000);
				sp(217819, 1769.4579f, 1290.9342f, 389.11728f, (byte) 80, 2000);
			break;
		}
	}
	
	private void sp(final int npcId, final float x, final float y, final float z, final byte h, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					spawn(npcId, x, y, z, h);
				}
			}
		}, time);
	}
	
	private void setEvent(StageType type, int time) {
		this.stageType = type;
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sendEventPacket();
			}
		}, time);
	}
}