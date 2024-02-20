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

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.model.instance.playerreward.CruciblePlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANT_DUNGEON_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_WORLD_SCENE_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300300000)
public class Empyrean_Crucible extends Crucible_System
{
	private byte stage;
	private Future<?> arminosTask;
	private Future<?> saamKingTask;
	private boolean isDoneStage4 = false;
	private boolean isDoneStage6Round2 = false;
	private boolean isDoneStage6Round1 = false;
	private List<Npc> npcs = new ArrayList<Npc>();
	private List<EmpyreanStage> empyreanStage = new ArrayList<EmpyreanStage>();
	
	protected final int IDARENA_STATUP_SPPAAD = 19624;
	
	@Override
	public void onEnterInstance(final Player player) {
		boolean isNew = !instanceReward.containPlayer(player.getObjectId());
		super.onEnterInstance(player);
		addItems(player);
		if (isNew && stage > 0) {
			moveToReadyRoom(player);
			///Training is in progress. You must stay in the Ready Room until you can join.
			sendMsgByRace(1401082, Race.PC_ALL, 2000);
		}
		CruciblePlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isPlayerLeave()) {
			onExitInstance(player);
			return;
		} else if (playerReward.isRewarded()) {
			doReward(player);
		}
		PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(instanceReward));
		PacketSendUtility.sendPacket(player, new S_WORLD_SCENE_STATUS(2, stageType.getId(), stageType.getType()));
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		stage = 0;
		sp(799567, 345.25107f, 349.40176f, 96.09097f, (byte) 0, 20000);
	}
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 217484: ///Instructor Munus.
			    final Npc instructorMunus = instance.getNpc(217484);
				///This Crucible certainly has lowered its standards.
				NpcShoutsService.getInstance().sendMsg(instructorMunus, 1500205, instructorMunus.getObjectId(), 0, 60000);
				///You're better than I thought.
				NpcShoutsService.getInstance().sendMsg(instructorMunus, 1500206, instructorMunus.getObjectId(), 0, 120000);
			break;
			case 217493: ///Instructor Geor.
			    final Npc instructorGeor = instance.getNpc(217493);
				///This Crucible certainly has lowered its standards.
				NpcShoutsService.getInstance().sendMsg(instructorGeor, 1500205, instructorGeor.getObjectId(), 0, 60000);
				///You're better than I thought.
				NpcShoutsService.getInstance().sendMsg(instructorGeor, 1500206, instructorGeor.getObjectId(), 0, 120000);
			break;
			case 217744: ///Administrator Arminos.
			    final Npc arminos3th = instance.getNpc(217744);
				///Welcome, Daeva. I am Administrator Arminos. The Empyrean Lords have made it clear that I am to assist you in your training.
				NpcShoutsService.getInstance().sendMsg(arminos3th, 1500247, arminos3th.getObjectId(), 0, 5000);
				///Let us see how you fare against my spirits... They are wild, bedeviling creatures, so watch your hide, Daeva.
				NpcShoutsService.getInstance().sendMsg(arminos3th, 1500250, arminos3th.getObjectId(), 0, 15000);
				///Well, well, well...that wasn't too bad. Might even call that interesting.
				NpcShoutsService.getInstance().sendMsg(arminos3th, 1500251, arminos3th.getObjectId(), 0, 25000);
			break;
			case 217749: ///Administrator Arminos.
			    final Npc arminos4th = instance.getNpc(217749);
				///I'm glad you've returned. Last time was...rather disappointing.
				NpcShoutsService.getInstance().sendMsg(arminos4th, 1500252, arminos4th.getObjectId(), 0, 5000);
				///This time your foes will be...more lively. They are Drakies under my command. The more you dispose of, the tastier the prize.
				NpcShoutsService.getInstance().sendMsg(arminos4th, 1500253, arminos4th.getObjectId(), 0, 15000);
				///Wonderful! Simply wonderful! If you ever return, I think I will face you personally.
				NpcShoutsService.getInstance().sendMsg(arminos4th, 1500255, arminos4th.getObjectId(), 0, 60000);
			break;
			case 799567: ///Record Keeper.
			    final Npc recordKeeper1 = instance.getNpc(799567);
				///Ready to start running Crucible, nyerk?
				NpcShoutsService.getInstance().sendMsg(recordKeeper1, 1111450, recordKeeper1.getObjectId(), 0, 2000);
			break;
			case 799568: ///Record Keeper.
			    final Npc recordKeeper2 = instance.getNpc(799568);
				///You have completed Stage 1, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper2, 1111460, recordKeeper2.getObjectId(), 0, 2000);
				///I hope you got yourself a Worthiness Ticket, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper2, 1111451, recordKeeper2.getObjectId(), 0, 6000);
			break;
			case 799569: ///Record Keeper.
			    final Npc recordKeeper3 = instance.getNpc(799569);
				///You have completed Stage 2, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper3, 1111461, recordKeeper3.getObjectId(), 0, 2000);
				///Stage 3 begins, nyerk!
				NpcShoutsService.getInstance().sendMsg(recordKeeper3, 1111452, recordKeeper3.getObjectId(), 0, 6000);
			break;
			case 205331: ///Record Keeper.
			    final Npc recordKeeper4 = instance.getNpc(205331);
				///You have completed Stage 3, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper4, 1111462, recordKeeper4.getObjectId(), 0, 2000);
				///Hope you are ready, because Stage 4 is about to begin!
				NpcShoutsService.getInstance().sendMsg(recordKeeper4, 1111453, recordKeeper4.getObjectId(), 0, 6000);
			break;
			case 205332: ///Record Keeper.
			    final Npc recordKeeper5 = instance.getNpc(205332);
				///Good progress. Let me know when you want to begin Stage 5!
				NpcShoutsService.getInstance().sendMsg(recordKeeper5, 1111454, recordKeeper5.getObjectId(), 0, 2000);
			break;
			case 205333: ///Record Keeper.
			    final Npc recordKeeper6 = instance.getNpc(205333);
				///Five down! Stage 6 about to begin!
				NpcShoutsService.getInstance().sendMsg(recordKeeper6, 1111455, recordKeeper6.getObjectId(), 0, 2000);
			break;
			case 205334: ///Record Keeper.
			    final Npc recordKeeper7 = instance.getNpc(205334);
				///Focus. Stage 7 will be more difficult!
				NpcShoutsService.getInstance().sendMsg(recordKeeper7, 1111456, recordKeeper7.getObjectId(), 0, 2000);
			break;
			case 205335: ///Record Keeper.
			    final Npc recordKeeper8 = instance.getNpc(205335);
				///Stage 8 ready for you! Are you ready for it?
				NpcShoutsService.getInstance().sendMsg(recordKeeper8, 1111457, recordKeeper8.getObjectId(), 0, 2000);
			break;
			case 205336: ///Record Keeper.
			    final Npc recordKeeper9 = instance.getNpc(205336);
				///Stay sharp. Tell me when you're ready for Stage 9, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper9, 1111458, recordKeeper9.getObjectId(), 0, 2000);
			break;
			case 205337: ///Record Keeper.
			    final Npc recordKeeper10 = instance.getNpc(205337);
				///Are you ready for final Stage 10?
				NpcShoutsService.getInstance().sendMsg(recordKeeper10, 1111459, recordKeeper10.getObjectId(), 0, 2000);
			break;
			case 205338: ///Record Keeper.
			    final Npc recordKeeper11 = instance.getNpc(205338);
				///You have completed Stage 4, nyerk.
				NpcShoutsService.getInstance().sendMsg(recordKeeper11, 1111463, recordKeeper11.getObjectId(), 0, 2000);
			break;
			case 205339: ///Record Keeper.
			    final Npc recordKeeper12 = instance.getNpc(205339);
				///Congratulations, you have passed Stage 5!
				NpcShoutsService.getInstance().sendMsg(recordKeeper12, 1111464, recordKeeper12.getObjectId(), 0, 2000);
			break;
			case 205340: ///Record Keeper.
			    final Npc recordKeeper13 = instance.getNpc(205340);
				///Congratulations, you have passed Stage 6!
				NpcShoutsService.getInstance().sendMsg(recordKeeper13, 1111465, recordKeeper13.getObjectId(), 0, 2000);
			break;
			case 205341: ///Record Keeper.
			    final Npc recordKeeper14 = instance.getNpc(205341);
				///Congratulations, you have passed Stage 7!
				NpcShoutsService.getInstance().sendMsg(recordKeeper14, 1111466, recordKeeper14.getObjectId(), 0, 2000);
			break;
			case 205342: ///Record Keeper.
			    final Npc recordKeeper15 = instance.getNpc(205342);
				///Great! You passed Stage 8!
				NpcShoutsService.getInstance().sendMsg(recordKeeper15, 1111467, recordKeeper15.getObjectId(), 0, 2000);
			break;
			case 205343: ///Record Keeper.
			    final Npc recordKeeper16 = instance.getNpc(205343);
				///Excellent! You passed Stage 9!
				NpcShoutsService.getInstance().sendMsg(recordKeeper16, 1111468, recordKeeper16.getObjectId(), 0, 2000);
			break;
			case 205344: ///Record Keeper.
			    final Npc recordKeeper17 = instance.getNpc(205344);
				///Wonderful, Daeva! You completed entire Crucible! Very impressive, nyerk!
				NpcShoutsService.getInstance().sendMsg(recordKeeper17, 1111469, recordKeeper17.getObjectId(), 0, 2000);
			break;
			case 282438: ///Glowing Dimensional Vortex.
			case 282439: ///Umbral Dimensional Vortex.
			    spawn(282374, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
			break;
		}
	}
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			///[RECORD KEEPER]
			case 799567: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_1_ELEVATOR);
				} else if (dialogId == 10001) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_7);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 799568: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_2_ELEVATOR);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 799569: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_3_ELEVATOR);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205331: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_4_ELEVATOR);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205338: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_5);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205332: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					switch (Rnd.get(1, 2)) {
					    case 1:
						    onChangeStage(StageType.START_AZOTURAN_STAGE_5_ROUND_1);
					    break;
					    case 2:
						    onChangeStage(StageType.START_STEEL_RAKE_STAGE_5_ROUND_1);
					    break;
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205339: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_6);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205333: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_6_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205340: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_7);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205334: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_7_ROUND_1);
					switch (player.getRace()) {
					    case ELYOS:
						    sp(217582, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 3000);
					    break;
					    case ASMODIANS:
						    sp(217578, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 3000);
					    break;
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205341: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_8);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205335: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_8_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205342: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_9);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205336: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_9_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205343: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_10);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205337: ///Record Keeper.
				if (dialogId == 10000) {
					despawnNpc(npc);
					onChangeStage(StageType.START_STAGE_10_ROUND_1);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205344: ///Record Keeper.
				if (dialogId == 10000) {
					onStopTraining(player);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			///[ARBITER]
			case 799573: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage1_4(player, 358.0000f, 349.0000f, 96.0000f, (byte) 59);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205426: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage5(player, 1260.0000f, 812.0000f, 358.0000f, (byte) 90);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205427: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage6(player, 1616.0000f, 154.0000f, 126.0000f, (byte) 10);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205428: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage7(player, 1793.0000f, 796.0000f, 469.0000f, (byte) 60);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205429: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage8(player, 1776.0000f, 1749.0000f, 303.0000f, (byte) 0);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205430: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage9(player, 1328.0000f, 1742.0000f, 316.0000f, (byte) 0);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 205431: ///Arbiter.
				if (dialogId == 10000) {
					if (player.getInventory().decreaseByItemId(186000124, 1)) { ///Worthiness Ticket.
					    crucibleStage10(player, 1760.0000f, 1278.0000f, 394.0000f, (byte) 0);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///"Player Name" has reentered the Illusion Stadium.
				        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400964, player.getName()));
					}
				});
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	private void addItems(Player player) {
        ItemService.addItem(player, 186000124, 3); //Worthiness Ticket.
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 701386: //Feed Supply Device.
				despawnNpc(npc);
				Npc goldenEyeMantutu1 = instance.getNpc(217553); //Golden Eye Mantutu.
				if (goldenEyeMantutu1 != null) {
					goldenEyeMantutu1.getAi2().think();
					goldenEyeMantutu1.getEffectController().removeEffect(18180); //Hunger.
				}
			break;
			case 701387: //Water Supply Device.
				despawnNpc(npc);
				Npc goldenEyeMantutu2 = instance.getNpc(217553); //Golden Eye Mantutu.
				if (goldenEyeMantutu2 != null) {
					goldenEyeMantutu2.getAi2().think();
					goldenEyeMantutu2.getEffectController().removeEffect(18181); //Thirst.
				}
			break;
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		if (npcs.contains(npc)) {
			npcs.remove(npc);
		}
		EmpyreanStage es = getEmpyreanStage(npc);
		int score = 0;
		switch (npc.getNpcId()) {
			//STAGE 1
			case 217477:
			case 217486:
			    score += 600;
			break;
			case 217478:
			case 217487:
			    score += 1200;
			break;
			case 217480:
			case 217489:
			    score += 500;
			break;
			case 217482:
			case 217491:
			    score += 1300;
			break;
			case 217483:
			case 217492:
			    score += 1100;
			break;
			case 217484:
			case 217493:
			    score += 5000;
			break;
			//STAGE 2
			case 217494:
			case 217497:
			case 217499:
			case 217504:
			case 217507:
			case 217508:
			    score += 300;
			break;
			case 217495:
			case 217498:
			case 217502:
			case 217505:
			case 217506:
			    score += 400;
			break;
			case 217496:
			case 217503:
			    score += 500;
			break;
			case 217500:
			case 217509:
			    score += 1500;
			break;
			case 217501:
			case 217510:
			    score += 5500;
			break;
			//STAGE 3
			case 217511:
            case 217512:
            case 217513:
            case 217514:
			    score += 55;
            break;
			case 217515:
			case 217516:
			case 217517:
			    score += 120;
            break;
			case 217518:
			    score += 110;
            break;
			case 217519:
			case 217520:
			case 217521:
			    score += 300;
            break;
			case 217522:
			    score += 250;
            break;
			case 217523:
			case 217524:
			case 217525:
			case 217526:
			    score += 475;
            break;
			case 217527:
			case 217528:
			    score += 3250;
            break;
			//STAGE 4
			case 217557:
			case 217558:
			case 217559:
			    score += 280;
            break;
			case 217560:
			case 217561:
			    score += 260;
            break;
			case 217562:
			    score += 340;
            break;
			case 217563:
			case 217566:
			    score += 400;
            break;
			case 217564:
			case 217565:
			    score += 350;
            break;
			case 217567:
			    score += 2100;
            break;
			case 217651:
			    score += 695;
            break;
			case 217652:
			    score += 590;
            break;
			case 217653:
			    score += 500;
            break;
			case 217654:
			    score += 455;
            break;
			//STAGE 5
			case 217529:
			case 217531:
			case 217545:
			case 217547:
			case 217548:
			case 217549:
			case 217550:
			    score += 300;
            break;
			case 217530:
			    score += 350;
            break;
			case 217532:
			case 217533:
			case 217534:
			case 217535:
			    score += 500;
            break;
			case 217536:
			    score += 400;
            break;
			case 217543:
			case 217553:
			    score += 2000;
            break;
			case 217544:
			case 217555:
			case 217556:
			    score += 7800;
            break;
			case 217551:
			case 217552:
			    score += 1900;
            break;
			case 217554:
			    score += 2400;
            break;
			//STAGE 6
			case 217568:
			    score += 480;
            break;
			case 217569:
			    score += 300;
            break;
			case 217570:
			    score += 700;
            break;
			case 217572:
			    score += 3370;
            break;
			case 217573:
			    score += 8900;
            break;
			//STAGE 7
			case 217578:
			case 217582:
			    score += 2900;
            break;
			case 217579:
			case 217583:
			    score += 3100;
            break;
			case 217580:
			case 217584:
			    score += 3800;
            break;
			case 217581:
			case 217585:
			    score += 3400;
            break;
			case 217586:
			case 217587:
			    score += 11500;
            break;
            //STAGE 8
			case 217588:
			case 217589:
			    score += 4000;
            break;
			case 217590:
			    score += 4200;
            break;
			case 217591:
			    score += 5000;
            break;
			case 217592:
			    score += 6000;
            break;
			case 217593:
			    score += 17000;
            break;
			//STAGE 9
			case 217594:
			    score += 6200;
            break;
			case 217595:
			    score += 7300;
            break;
			case 217596:
			case 217597:
			    score += 4250;
            break;
			case 217598:
			    score += 9400;
            break;
			case 217599:
			    score += 25900;
            break;
			//STAGE 10
			case 217600:
			case 217601:
			case 217602:
			    score += 1090;
            break;
			case 217603:
			case 217604:
			case 217605:
			case 217606:
			    score += 1390;
            break;
			case 217607:
			    score += 17800;
            break;
			case 217608:
			    score += 192500;
            break;
			case 217609:
			    score += 204400;
            break;
		} if (score != 0) {
			sendPacket(score, npc.getObjectTemplate().getNameId());
		} switch (npc.getNpcId()) {
			case 217486:
			case 217489:
                despawnNpc(npc);
                if (getNpc(217486) == null && getNpc(217489) == null) {
                    sendEventPacket(StageType.START_STAGE_1_ROUND_2, 2000);
					sp(217492, 332.7714f, 358.48206f, 96.09092f, (byte) 106, 2000);
                }
            break;
            case 217492:
                despawnNpc(npc);
                sendEventPacket(StageType.START_STAGE_1_ROUND_3, 2000);
				sp(217487, 334.844f, 339.92618f, 96.09094f, (byte) 18, 2000);
            break;
            case 217487:
                despawnNpc(npc);
                sendEventPacket(StageType.START_STAGE_1_ROUND_4, 2000);
				sp(217491, 341.03156f, 361.04315f, 96.09093f, (byte) 90, 2000);
            break;
            case 217491:
                despawnNpc(npc);
                sendEventPacket(StageType.START_STAGE_1_ROUND_5, 2000);
				sp(217493, 332.09300f, 349.36847f, 96.09093f, (byte) 0, 2000);
            break;
			case 217484: //Instructor Munus.
			case 217493: //Instructor Geor.
				despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_1, 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(1), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(1), 4000);
					}
				});
				spawn(799568, 345.25000f, 349.24000f, 96.09097f, (byte) 0); //Empyrean RecordKeeper.
				///A Worthiness Ticket Box has appeared in the Illusion Stadium.
				sendMsgByRace(1400975, Race.PC_ALL, 6000);
				//Worthiness Ticket Box.
                spawn(217756, 345.66763f, 355.47922f, 96.154961f, (byte) 0, 79);
				spawn(217756, 349.04822f, 357.50226f, 96.154961f, (byte) 0, 280);
				spawn(217756, 345.66718f, 359.32202f, 96.154961f, (byte) 0, 282);
                spawn(217756, 342.62552f, 357.31348f, 96.154961f, (byte) 0, 283);
				spawn(217756, 342.56638f, 353.64630f, 96.154961f, (byte) 0, 289);
				spawn(217756, 342.57877f, 360.80563f, 96.154961f, (byte) 0, 290);
			break;
			case 217502:
			    despawnNpc(npc);
			    switch (stageType) {
				    case START_STAGE_2_ROUND_1:
						if (getNpc(217503) == null &&
							getNpc(217504) == null) {
							startStage2Round2();
						}
				    break;
				    case START_STAGE_2_ROUND_2:
						if (getNpc(217508) == null &&
							getNpc(217507) == null &&
							getNpc(217504) == null) {
							startStage2Round3();
						}
				    break;
			    }
			break;
			case 217503:
			    despawnNpc(npc);
			    if (stageType == StageType.START_STAGE_2_ROUND_1 &&
			        getNpc(217502) == null &&
				    getNpc(217504) == null) {
				    startStage2Round2();
			    }
			break;
			case 217504:
			    despawnNpc(npc);
			    switch (stageType) {
				    case START_STAGE_2_ROUND_1:
						if (getNpc(217502) == null &&
							getNpc(217503) == null) {
							startStage2Round2();
						}
				    break;
				    case START_STAGE_2_ROUND_2:
						if (getNpc(217502) == null &&
							getNpc(217507) == null &&
							getNpc(217508) == null) {
							startStage2Round3();
						}
				    break;
				}
			break;
			case 217507:
			    despawnNpc(npc);
			    switch (stageType) {
				    case START_STAGE_2_ROUND_2:
						if (getNpc(217502) == null &&
							getNpc(217504) == null &&
							getNpc(217508) == null) {
							startStage2Round3();
						}
				    break;
			    } if (es != null && !es.containNpc()) {
				    startStage2Round5();
			    }
			break;
			case 217508:
			    despawnNpc(npc);
			    if (es != null) {
				    return;
			    } switch (stageType) {
			        case START_STAGE_2_ROUND_2:
			            if (getNpc(217502) == null &&
				            getNpc(217504) == null &&
					        getNpc(217507) == null) {
					        startStage2Round3();
						}
					break;
					case START_STAGE_2_ROUND_4:
					    if (getNpc(217505) == null) {
					        startStage2Round4();
						}
					break;
				}
			break;
			case 217500:
			case 217509:
				despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_2_ROUND_4, 2000);
				sp(217505, 341.95056f, 334.77692f, 96.09093f, (byte) 0, 2000);
                sp(217508, 344.17813f, 334.42462f, 96.090935f, (byte) 0, 2000);
			break;
			case 217505:
			    despawnNpc(npc);
			    if (getNpc(217508) == null) {
				    startStage2Round4();
			    }
			break;
			case 217506:
			    despawnNpc(npc);
			    if (es != null && !es.containNpc()) {
				   startStage2Round5();
			    }
			break;
			case 217501:
			case 217510:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(280470));
				sendEventPacket(StageType.START_BONUS_STAGE_2, 2000);
				///You can earn an additional reward if you catch the Saam King.
				sendMsgByRace(1400978, Race.PC_ALL, 5000);
				///King Saam will disappear in 30 seconds!
				sendMsgByRace(1400979, Race.PC_ALL, 32000);
				sp(217737, 334.49496f, 349.23220f, 96.090935f, (byte) 0, 2000);
				saamKingTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (getNpc(217737) != null) {
							killNpc(getNpcs(217738));
							despawnNpcs(instance.getNpcs(217737));
							sendEventPacket(StageType.PASS_GROUP_STAGE_2, 0);
							sp(799569, 345.25000f, 349.24000f, 96.090970f, (byte) 0, 2000);
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
					}
				}, 62000);
			break;
			case 217737:
			    killNpc(getNpcs(217738));
				saamKingTask.cancel(true);
				sendEventPacket(StageType.PASS_GROUP_STAGE_2, 0);
				sp(799569, 345.25000f, 349.24000f, 96.090970f, (byte) 0, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(2), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(2), 4000);
					}
				});
			break;
			case 217511:
			case 217512:
			case 217513:
			case 217514:
				despawnNpc(npc);
				if (getNpcs(217511).isEmpty() &&
				    getNpcs(217512).isEmpty() &&
					getNpcs(217513).isEmpty() &&
					getNpcs(217514).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_3_ROUND_2, 2000);
					sp(217515, 336.32092f, 345.0251f, 96.090935f, (byte) 0, 6000);
					sp(217516, 347.16144f, 361.89084f, 96.09093f, (byte) 0, 6000);
					sp(217518, 352.77557f, 360.97845f, 96.09091f, (byte) 0, 6000);
					sp(217518, 340.2231f, 351.10208f, 96.09098f, (byte) 0, 6000);
					sp(217517, 354.132f, 337.14255f, 96.09089f, (byte) 0, 6000);
					sp(217517, 353.7888f, 354.4324f, 96.091064f, (byte) 0, 6000);
					sp(217516, 350.0108f, 342.09482f, 96.090935f, (byte) 0, 6000);
					sp(217515, 349.16327f, 335.63864f, 96.09095f, (byte) 0, 6000);
					sp(217517, 341.23633f, 344.55603f, 96.09096f, (byte) 0, 6000);
					sp(217518, 354.66513f, 343.31537f, 96.091095f, (byte) 0, 6000);
					sp(217516, 334.60898f, 352.01447f, 96.09095f, (byte) 0, 6000);
					sp(217515, 348.87338f, 354.90146f, 96.09096f, (byte) 0, 6000);
				}
			break;
			case 217515:
			case 217516:
			case 217517:
			case 217518:
			    despawnNpc(npc);
			    if (getNpcs(217515).isEmpty() &&
			        getNpcs(217516).isEmpty() &&
				    getNpcs(217517).isEmpty() &&
				    getNpcs(217518).isEmpty()) {
				    sendEventPacket(StageType.START_STAGE_3_ROUND_3, 2000);
				    sp(217519, 351.08026f, 341.61298f, 96.090935f, (byte) 0, 2000);
				    sp(217521, 333.4532f, 354.7357f, 96.09094f, (byte) 0, 2000);
				    sp(217522, 342.1805f, 360.534f, 96.09092f, (byte) 0, 2000);
				    sp(217520, 334.2686f, 342.60797f, 96.09091f, (byte) 0, 2000);
				    sp(217522, 350.34537f, 356.18558f, 96.09094f, (byte) 0, 2000);
				    sp(217520, 343.7485f, 336.2869f, 96.09092f, (byte) 0, 2000);
			    }
			break;
			case 217519:
			case 217520:
			case 217521:
			case 217522:
			    despawnNpc(npc);
				if (getNpcs(217519).isEmpty() &&
				    getNpcs(217520).isEmpty() &&
					getNpcs(217521).isEmpty() &&
					getNpcs(217522).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_3_ROUND_4, 2000);
					sp(217524, 349.66446f, 341.4752f, 96.090965f, (byte) 0, 2000);
					sp(217525, 338.32742f, 356.29636f, 96.090935f, (byte) 0, 2000);
					sp(217526, 349.31473f, 358.43762f, 96.09096f, (byte) 0, 2000);
					sp(217523, 338.73138f, 342.35876f, 96.09094f, (byte) 0, 2000);
				}
			break;
			case 217523:
			case 217524:
			case 217525:
			case 217526:
			    despawnNpc(npc);
			    if (getNpcs(217523).isEmpty() &&
			        getNpcs(217524).isEmpty() &&
				    getNpcs(217525).isEmpty() &&
				    getNpcs(217526).isEmpty()) {
				    sendEventPacket(StageType.START_STAGE_3_ROUND_5, 2000);
				    sp(217527, 335.37524f, 346.34567f, 96.09094f, (byte) 0, 2000);
				    sp(217528, 335.36105f, 353.16922f, 96.09094f, (byte) 0, 2000);
			    }
			break;
			case 217527:
			case 217528:
			    despawnNpc(npc);
			    if (getNpcs(217527).isEmpty() &&
			        getNpcs(217528).isEmpty()) {
				    sendEventPacket(StageType.START_BONUS_STAGE_3, 2000);
				    sp(217744, 342.45215f, 349.339f, 96.09096f, (byte) 0, 2000); //Administrator Arminos.
				    ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    startBonusStage3();
					    }
				    }, 39000);
			    }
			break;
			case 217557:
			case 217559:
			case 217562:
			    despawnNpc(npc);
			    switch (stageType) {
				    case START_STAGE_4_ROUND_1:
						if (getNpcs(217557).isEmpty() &&
							getNpcs(217559).isEmpty()) {
							sp(217558, 330.27792f, 339.2779f, 96.09093f, (byte) 6);
							sp(217558, 328.08972f, 346.3553f, 96.090904f, (byte) 1);
						}
				    break;
				    case START_STAGE_4_ROUND_2:
				        if (es!= null && !es.containNpc()) {
					        startStage4Round3();
				        }
				    break;
			    }
			break;
			case 217558:
			case 217561:
			    despawnNpc(npc);
			    switch (stageType) {
				    case START_STAGE_4_ROUND_1:
					    if (getNpcs(217558).isEmpty()) {
						    sendEventPacket(StageType.START_STAGE_4_ROUND_2, 2000);
						    sp(217559, 330.53665f, 349.23523f, 96.09093f, (byte) 0, 6000);
						    sp(217562, 334.89508f, 363.78442f, 96.090904f, (byte) 105, 6000);
						    sp(217560, 334.61942f, 334.80353f, 96.090904f, (byte) 15, 6000);
						    ThreadPoolManager.getInstance().schedule(new Runnable() {
							    @Override
							    public void run() {
								    List<Npc> round = new ArrayList<Npc>();
								    round.add(sp(217557, 357.24625f, 338.30093f, 96.09104f, (byte) 65));
								    round.add(sp(217558, 357.20663f, 359.28714f, 96.091064f, (byte) 75));
								    round.add(sp(217561, 365.109f, 349.1218f, 96.09114f, (byte) 60));
								    empyreanStage.add(new EmpyreanStage(round));
							    }
						    }, 47000);
					    }
				    break;
				    case START_STAGE_4_ROUND_2:
				        if (es!= null && !es.containNpc()) {
					        startStage4Round3();
				        }
				    break;
				}
			break;
			case 217563:
			case 217565:
			case 217566:
				despawnNpc(npc);
				if (es != null && !es.containNpc()) {
					sendEventPacket(StageType.START_STAGE_4_ROUND_4, 2000);
					sp(217567, 345.73895f, 349.49786f, 96.09097f, (byte) 0, 6000);
				}
			break;
			case 217564:
			case 217560:
			case 217745:
			case 217746:
			case 217747:
			case 217576:
			case 217577:
				despawnNpc(npc);
			break;
			case 217567:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(280685));
				despawnNpcs(instance.getNpcs(280686));
				despawnNpcs(instance.getNpcs(280687));
				sendEventPacket(StageType.START_STAGE_4_ROUND_5, 2000);
				///A large number of Balaur Troopers descend from the Dredgion.
				sendMsgByRace(1400999, Race.PC_ALL, 6000);
				///A large number of Balaur Troopers descend from the Dredgion.
				sendMsgByRace(1400999, Race.PC_ALL, 69000);
				///A large number of Balaur Troopers descend from the Dredgion.
				sendMsgByRace(1400999, Race.PC_ALL, 142000);
				sp(217653, 327.7691f, 349.2621f, 96.0909f, (byte) 0, 6000);
				sp(217651, 364.8972f, 349.2565f, 96.0911f, (byte) 60, 18000);
				sp(217652, 361.1795f, 339.9925f, 96.0911f, (byte) 50, 35000);
				sp(217653, 354.4119f, 333.6749f, 96.0909f, (byte) 40, 54000);
				sp(217651, 331.6150f, 358.4374f, 96.0909f, (byte) 110, 69000);
				sp(217652, 338.3885f, 364.9150f, 96.0909f, (byte) 100, 83000);
				sp(217651, 346.3984f, 368.1942f, 96.0909f, (byte) 90, 99000);
				sp(217652, 353.9260f, 364.9263f, 96.0909f, (byte) 80, 110000);
				sp(217653, 361.1345f, 358.9042f, 96.0911f, (byte) 65, 130000);
				sp(217652, 346.3440f, 329.9449f, 96.0909f, (byte) 30, 142000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						sp(217653, 331.53894f, 339.88320f, 96.09091f, (byte) 10);
						isDoneStage4 = true;
					}
				}, 174000);
			break;
			case 217651:
			case 217652:
			case 217653:
				despawnNpc(npc);
				if (isDoneStage4 &&
				    getNpcs(217651).isEmpty() &&
				    getNpcs(217652).isEmpty() &&
					getNpcs(217653).isEmpty()) {
					sp(217749, 340.59000f, 349.32166f, 96.09096f, (byte) 0, 2000); //Administrator Arminos.
					///The Drakies will appear soon!
		            sendMsgByRace(1400982, Race.PC_ALL, 5000);
					///3...
					sendMsgByRace(1400988, Race.PC_ALL, 30000);
					///2...
					sendMsgByRace(1400989, Race.PC_ALL, 31000);
					///1...
					sendMsgByRace(1400990, Race.PC_ALL, 32000);
					sendEventPacket(StageType.START_BONUS_STAGE_4, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							startBonusStage4();
						}
					}, 33000);
				}
			break;
		   /**
			* STAGE 5 [Azoturan Version]
			*/
			case 217529:
				despawnNpc(npc);
				if (getNpcs(217529).isEmpty()) {
				    sendEventPacket(StageType.START_STAGE_5_ROUND_2, 2000);
				    sp(217530, 1263.1987f, 778.4129f, 358.6056f, (byte) 30, 4000);
					sp(217531, 1260.1381f, 778.84644f, 358.60562f, (byte) 30, 4000);
					sp(217530, 1257.3065f, 778.35016f, 358.60562f, (byte) 30, 4000);
				}
			break;
			case 217530:
			case 217531:
				despawnNpc(npc);
				if (getNpcs(217530).isEmpty() &&
				    getNpcs(217531).isEmpty()) {
				    sendEventPacket(StageType.START_STAGE_5_ROUND_3, 2000);
				    sp(217543, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000);
				}
			break;
			case 217543:
			    despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_5_ROUND_4, 2000);
				sp(217532, 1246.4855f, 796.90735f, 358.6056f, (byte) 2000);
                sp(217533, 1259.5508f, 784.5548f, 358.60562f, (byte) 3000);
                sp(217534, 1276.6561f, 812.5499f, 358.60565f, (byte) 4000);
                sp(217535, 1243.2113f, 813.0927f, 358.60565f, (byte) 5000);
                sp(217536, 1272.9266f, 797.1055f, 358.60562f, (byte) 6000);
			break;
			case 217532:
			case 217533:
			case 217534:
			case 217535:
			case 217536:
				despawnNpc(npc);
				if (getNpcs(217532).isEmpty() &&
				    getNpcs(217533).isEmpty() &&
					getNpcs(217534).isEmpty() &&
					getNpcs(217535).isEmpty() &&
				    getNpcs(217536).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_5_ROUND_5, 2000);
					sp(217544, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000);
				}
			break;
			case 217544:
				despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_5, 0);
				sp(205339, 1260.1465f, 795.07495f, 358.60562f, (byte) 30);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(5), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(5), 4000);
					}
				});
			break;
		   /**
			* STAGE 5 [Steel Rake Version]
			*/
			case 217547:
			case 217548:
			case 217549:
				despawnNpc(npc);
				if (getNpcs(217547).isEmpty() &&
				    getNpcs(217548).isEmpty() &&
				    getNpcs(217549).isEmpty()) {
					sp(217550, 1266.293f, 778.3254f, 358.60574f, (byte) 30, 4000);
					sp(217545, 1254.261f, 778.3817f, 358.6056f, (byte) 30, 4000);
				}
			break;
			case 217550:
			case 217545:
				despawnNpc(npc);
				if (getNpcs(217550).isEmpty() &&
				    getNpcs(217545).isEmpty()) {
				    sendEventPacket(StageType.START_STAGE_5_ROUND_2, 2000);
				    switch (Rnd.get(1, 2)) {
				        case 1:
					        sp(217551, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000); //Freed Genie.
						break;
						case 2:
						    sp(217552, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000); //Madame Bovariki.
						break;
					}
				}
			break;
			case 217551: //Freed Genie.
			case 217552: //Madame Bovariki.
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282443));
				sendEventPacket(StageType.START_STAGE_5_ROUND_3, 2000);
				sp(217553, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000);
			break;
			case 217553:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(701386)); //Feed Supply Device.
                despawnNpcs(instance.getNpcs(701387)); //Water Supply Device.
				sendEventPacket(StageType.START_STAGE_5_ROUND_4, 2000);
				switch (Rnd.get(1, 2)) {
					case 1:
					    sp(281108, 1260.0030f, 810.5550f, 358.6056f, (byte) 0);
						sp(281109, 1272.9266f, 797.1055f, 358.6056f, (byte) 0);
						sp(281110, 1259.5508f, 784.5548f, 358.6056f, (byte) 0);
						sp(281111, 1246.4855f, 796.9073f, 358.6056f, (byte) 0);
						sp(281112, 1276.6561f, 812.5499f, 358.6056f, (byte) 0);
						sp(281113, 1275.8940f, 780.5154f, 358.6056f, (byte) 0);
						sp(281114, 1244.3293f, 780.4284f, 358.6056f, (byte) 0);
						sp(217554, 1260.0372f, 797.8033f, 358.6056f, (byte) 0, 2000); //Engineer Lahulahu.
					break;
					case 2:
						sp(217555, 1260.0372f, 797.8033f, 358.6056f, (byte) 0, 2000); //Chief Gunner Koakoa.
					break;
				}
			break;
			case 217554:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(281108));
				despawnNpcs(instance.getNpcs(281109));
				despawnNpcs(instance.getNpcs(281110));
				despawnNpcs(instance.getNpcs(281111));
				despawnNpcs(instance.getNpcs(281112));
				despawnNpcs(instance.getNpcs(281113));
				despawnNpcs(instance.getNpcs(281114));
				sendEventPacket(StageType.START_STAGE_5_ROUND_5, 2000);
				sp(217556, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000);
			break;
			case 217555: //Chief Gunner Koakoa.
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(281212));
				despawnNpcs(instance.getNpcs(281213));
				despawnNpcs(instance.getNpcs(281331));
				sendEventPacket(StageType.START_STAGE_5_ROUND_5, 2000);
				sp(217556, 1260.0372f, 796.80334f, 358.60562f, (byte) 30, 4000);
			break;
			case 217556:
				despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_5, 0);
				sp(205339, 1260.1465f, 795.07495f, 358.60562f, (byte) 30);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(5), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(5), 4000);
					}
				});
			break;
			case 217568:
				despawnNpc(npc);
				if (isDoneStage6Round1 && getNpcs(217568).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_6_ROUND_2, 2000);
					sp(217570, 1629.4642f, 154.8044f, 126f, (byte) 30, 6000);
					sp(217569, 1643.7776f, 161.63562f, 126f, (byte) 46, 6000);
					sp(217569, 1639.7843f, 142.09268f, 126f, (byte) 40, 6000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							sp(217569, 1614.6377f, 164.04999f, 126.00113f, (byte) 3);
							sp(217569, 1625.8965f, 135.62509f, 126f, (byte) 30);
							isDoneStage6Round2 = true;
						}
					}, 12000);
				}
			break;
			case 217569:
			case 217570:
				despawnNpc(npc);
				if (stageType == StageType.START_STAGE_6_ROUND_2 && isDoneStage6Round2 &&
				    getNpcs(217569).isEmpty() &&
					getNpcs(217570).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_6_ROUND_3, 2000);
					sp(217572, 1629.5837f, 138.38435f, 126f, (byte) 30, 2000);
                    sp(217569, 1635.01535f, 150.01535f, 126f, (byte) 45, 2000);
					sp(217569, 1638.3817f, 152.84074f, 126f, (byte) 45, 2000);
				}
			break;
			case 217572:
                despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_6_ROUND_4, 2000);
                sp(217573, 1626.7312f, 156.94821f, 126.0f, (byte) 91, 2000); //Spectral Warrior.
				///A Worthiness Ticket Box has appeared in the Ready Room.
				sendMsgByRace(1400977, Race.PC_ALL, 10000);
				sp(217758, 1595.9895f, 146.07008f, 128.77043f, (byte) 6, 10000); //Empyrean Box.
            break;
			case 217573: //Spectral Warrior.
                despawnNpc(npc);
				sendEventPacket(StageType.START_BONUS_STAGE_6, 2000);
				///Administrator Arminos will disappear in 30 seconds!
				sendMsgByRace(1401016, Race.PC_ALL, 0);
				///Administrator Arminos will disappear in 10 seconds!
				sendMsgByRace(1401017, Race.PC_ALL, 20000);
				///Administrator Arminos will disappear in 5 seconds!
				sendMsgByRace(1401018, Race.PC_ALL, 25000);
				sp(217750, 1626.73120f, 156.94821f, 126.00000f, (byte) 91, 2000);
				arminosTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (getNpc(217750) != null) {
							despawnNpcs(instance.getNpcs(217750));
							sendEventPacket(StageType.PASS_GROUP_STAGE_6, 0);
							sp(205340, 1629.75830f, 156.67245f, 126.00000f, (byte) 75, 0);
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									///You have eliminated all enemies in Round %0.
									PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(6), 2000);
									///You have passed Stage %0!
									PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(6), 4000);
								}
							});
						}
					}
				}, 32000);
            break;
			case 217750: //Administrator Arminos.
			    arminosTask.cancel(true);
			    despawnNpcs(instance.getNpcs(217751));
		        despawnNpcs(instance.getNpcs(217752));
				sendEventPacket(StageType.PASS_GROUP_STAGE_6, 0);
				sp(205340, 1629.75830f, 156.67245f, 126.00000f, (byte) 75, 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(6), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(6), 4000);
					}
				});
            break;
		   /**
			* Stage 7 is not same for player "Elyos/Asmodians"
			* [Asmodians Version]
			*/
			case 217578:
                despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_7_ROUND_2, 2000);
                sp(217579, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217579:
                despawnNpc(npc);
				sendMsg(1400929);
				sendEventPacket(StageType.START_STAGE_7_ROUND_3, 2000);
                sp(217580, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217580:
                despawnNpc(npc);
				sendMsg(1400929);
				despawnNpcs(instance.getNpcs(282363));
		        despawnNpcs(instance.getNpcs(282364));
				sendEventPacket(StageType.START_STAGE_7_ROUND_4, 2000);
                sp(217581, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217581:
                despawnNpc(npc);
				sendMsg(1400929);
				despawnNpcs(instance.getNpcs(282366));
				despawnNpcs(instance.getNpcs(282367));
				despawnNpcs(instance.getNpcs(282368));
				sendEventPacket(StageType.START_STAGE_7_ROUND_5, 2000);
                sp(217586, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217586:
                despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_7, 0);
                sp(205341, 1783.0873f, 796.8426f, 469.35013f, (byte) 0);
				sp(217759, 1784.4686f, 792.8891f, 469.35013f, (byte) 0); //Empyrean Box.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(7), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(7), 4000);
					}
				});
            break;
		   /**
			* [Elyos Version]
			*/
			case 217582:
                despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_7_ROUND_2, 2000);
                sp(217583, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217583:
                despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_7_ROUND_3, 2000);
                sp(217584, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217584:
                despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282363));
		        despawnNpcs(instance.getNpcs(282364));
				sendEventPacket(StageType.START_STAGE_7_ROUND_4, 2000);
                sp(217585, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217585:
                despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282369));
				despawnNpcs(instance.getNpcs(282370));
				despawnNpcs(instance.getNpcs(282371));
				sendEventPacket(StageType.START_STAGE_7_ROUND_5, 2000);
                sp(217587, 1783.0873f, 796.8426f, 469.35013f, (byte) 0, 2000);
            break;
            case 217587:
                despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_7, 0);
                sp(205341, 1783.0873f, 796.8426f, 469.35013f, (byte) 0);
				sp(217759, 1784.4686f, 792.8891f, 469.35013f, (byte) 0); //Empyrean Box.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(7), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(7), 4000);
					}
				});
            break;
			case 217588: //Kromede The Corrupt.
			case 217589: //Vile Judge Kromede.
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282372));
				sendEventPacket(StageType.START_STAGE_8_ROUND_2, 2000);
				sp(217590, 1794.9225f, 1756.2131f, 304.1f, (byte) 55, 2000);
			break;
			case 217590:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(280712));
				despawnNpcs(instance.getNpcs(280713));
				sendEventPacket(StageType.START_STAGE_8_ROUND_3, 2000);
				sp(217591, 1791.1407f, 1777.92f, 304.1f, (byte) 76, 2000);
			break;
			case 217591:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(280802));
				despawnNpcs(instance.getNpcs(280803));
				despawnNpcs(instance.getNpcs(280804));
				sendEventPacket(StageType.START_STAGE_8_ROUND_4, 2000);
				sp(217592, 1764.3282f, 1744.4377f, 304.1f, (byte) 80, 2000);
			break;
			case 217592:
				despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_8_ROUND_5, 2000);
				sp(217593, 1786.7078f, 1757.4915f, 303.8f, (byte) 49, 2000);
			break;
			case 217593:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282373));
				sendEventPacket(StageType.PASS_GROUP_STAGE_8, 0);
				sp(205342, 1776.757f , 1764.624f, 303.695f, (byte) 90);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(8), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(8), 4000);
					}
				});
			break;
			case 217594:
				despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_9_ROUND_2, 2000);
				sp(217595, 1328.4663f, 1711.2297f, 317.6f, (byte) 44, 2000);
			break;
			case 217595:
				despawnNpc(npc);
				despawnNpcs(instance.getNpcs(282378));
				sendEventPacket(StageType.START_STAGE_9_ROUND_3, 2000);
				sp(217596, 1311.87f, 1731.36f, 315.674f, (byte) 43, 2000);
				sp(217597, 1298.89f, 1743.77f, 316.07f, (byte) 108, 2000);
			break;
			case 217596:
			case 217597:
                final Npc counterpart = instance.getNpc(npc.getNpcId() == 217596 ? 217597 : 217596);
				if (counterpart != null && !NpcActions.isAlreadyDead(counterpart)) {
					SkillEngine.getInstance().getSkill(counterpart, IDARENA_STATUP_SPPAAD, 10, counterpart).useNoAnimationSkill();
				}
				despawnNpc(npc);
                if (getNpcs(217596).isEmpty() &&
				    getNpcs(217597).isEmpty()) {
					sendEventPacket(StageType.START_STAGE_9_ROUND_4, 2000);
                    sp(217598, 1311.5238f, 1755.2079f, 317.1f, (byte) 97, 2000);
                }
            break;
			case 217598:
			    despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_9_ROUND_5, 2000);
                sp(217599, 1304.2659f, 1722.2467f, 316.5f, (byte) 23, 2000);
            break;
            case 217599:
                despawnNpc(npc);
				sendEventPacket(StageType.PASS_GROUP_STAGE_9, 0);
				sp(205343, 1324.5742f, 1739.683f, 316.4109f, (byte) 8, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(9), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(9), 4000);
					}
				});
            break;
			case 217600:
			case 217601:
			case 217602:
                despawnNpc(npc);
				if (getNpcs(217600).isEmpty() &&
				    getNpcs(217601).isEmpty() &&
					getNpcs(217602).isEmpty()) {
                    sendEventPacket(StageType.START_STAGE_10_ROUND_2, 2000);
				    sp(217603, 1744.6332f, 1280.0349f, 394.3f, (byte) 9, 2000);
				    sp(217604, 1756.2661f, 1305.561f, 394.3f, (byte) 97, 6000);
					sp(217605, 1763.1177f, 1268.2404f, 394.3f, (byte) 22, 10000);
				    sp(217606, 1765.2681f, 1306.5621f, 394.3f, (byte) 89, 14000);
				}
			break;
			case 217603:
			case 217604:
			case 217605:
			case 217606:
                despawnNpc(npc);
				if (getNpcs(217603).isEmpty() &&
				    getNpcs(217604).isEmpty() &&
					getNpcs(217605).isEmpty() &&
					getNpcs(217606).isEmpty()) {
                    sendEventPacket(StageType.START_STAGE_10_ROUND_3, 2000);
					sp(217607, 1771.2675f, 1304.7964f, 394.3f, (byte) 82, 2000);
				}
			break;
			case 217607:
                despawnNpc(npc);
				sendEventPacket(StageType.START_STAGE_10_ROUND_4, 2000);
                sp(217608, 1754.8065f, 1303.702f, 394.3f, (byte) 100, 2000);
            break;
			case 217608:
			    despawnNpc(npc);
				despawnNpcs(instance.getNpcs(281258));
				despawnNpcs(instance.getNpcs(281259));
                sendEventPacket(StageType.START_STAGE_10_ROUND_5, 2000);
                sp(217609, 1765.6692f, 1288.092f, 394.3f, (byte) 30, 2000);
            break;
			case 217609:
			    despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217804));
				despawnNpcs(instance.getNpcs(282438));
				despawnNpcs(instance.getNpcs(282439));
				sendEventPacket(StageType.PASS_GROUP_STAGE_10, 0);
                sp(205344, 1764.6368f, 1288.831f, 394.23755f, (byte) 77);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(10), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(10), 4000);
					}
				});
            break;
		}
	}
	
	private void startBonusStage3() {
		sp(217740, 360.76f, 349.42f, 96.1f, (byte) 0);
		sp(217741, 346.27f, 363.35f, 96.1f, (byte) 11);
		sp(217742, 332.12f, 349.22f, 96.1f, (byte) 0);
		sp(217743, 346.42f, 335.1f, 96.1f, (byte) 87);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///Spirits will disappear in 30 seconds!
				sendMsgByRace(1401010, Race.PC_ALL, 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Spirits will disappear in 10 seconds!
						sendMsgByRace(1401011, Race.PC_ALL, 0);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								///Spirits will disappear in 5 seconds!
								sendMsgByRace(1401012, Race.PC_ALL, 0);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										despawnNpcs(instance.getNpcs(217740));
										despawnNpcs(instance.getNpcs(217741));
										despawnNpcs(instance.getNpcs(217742));
										despawnNpcs(instance.getNpcs(217743));
										despawnNpcs(instance.getNpcs(217744)); //Administrator Arminos.
										sendEventPacket(StageType.PASS_GROUP_STAGE_3, 0);
										///A Worthiness Ticket Box has appeared in the Ready Room.
										sendMsgByRace(1400976, Race.PC_ALL, 6000);
										sp(205331, 345.2500f, 349.24000f, 96.09097f, (byte) 0);
										sp(217735, 378.9331f, 346.74878f, 96.74762f, (byte) 0);
										instance.doOnAllPlayers(new Visitor<Player>() {
											@Override
											public void visit(Player player) {
												///You have eliminated all enemies in Round %0.
												PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(3), 2000);
												///You have passed Stage %0!
												PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(3), 4000);
											}
										});
									}
								}, 5000);
							}
						}, 5000);
					}
				}, 20000);
			}
		}, 30000);
	}
	
	private void startBonusStage4() {
		///Drakies will disappear in 30 seconds!
		sendMsgByRace(1401013, Race.PC_ALL, 90000);
		///Drakies will disappear in 10 seconds!
		sendMsgByRace(1401014, Race.PC_ALL, 110000);
		///Drakies will disappear in 5 seconds!
		sendMsgByRace(1401015, Race.PC_ALL, 115000);
		sp(217778, 346.0000f, 366.0000f, 96.0000f, (byte) 1, 0);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 3000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 6000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 9000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 12000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 15000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 18000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 21000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 24000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 27000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 30000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 33000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 36000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 39000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 42000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 45000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 48000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 51000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 54000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 57000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 60000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 63000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 66000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 69000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 72000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 75000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 78000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 81000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 84000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 87000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 90000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 93000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 96000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 99000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 102000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 105000);
		sp(217748, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 108000);
		sp(217745, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 111000);
		sp(217746, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 114000);
		sp(217747, 339.1944f, 367.0892f, 96.0909f, (byte) 60, 117000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				despawnNpcs(instance.getNpcs(217745));
				despawnNpcs(instance.getNpcs(217746));
				despawnNpcs(instance.getNpcs(217747));
				despawnNpcs(instance.getNpcs(217748));
				despawnNpcs(instance.getNpcs(217749)); //Administrator Arminos.
				despawnNpcs(instance.getNpcs(217778)); //Administrator Arminos Gate.
				sendEventPacket(StageType.PASS_GROUP_STAGE_4, 0);
				sp(205338, 345.2500f, 349.2400f, 96.0909f, (byte) 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///You have eliminated all enemies in Round %0.
				        PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_ROUND_IDARENA(4), 2000);
						///You have passed Stage %0!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_COMPLETE_STAGE_IDARENA(4), 4000);
					}
				});
			}
		}, 120000);
	}
	
	private void startStage2Round2() {
		sendEventPacket(StageType.START_STAGE_2_ROUND_2, 2000);
		sp(217502, 328.78433f, 348.77353f, 96.09092f, (byte) 0, 2000);
		sp(217508, 329.01874f, 343.79257f, 96.09092f, (byte) 0, 2000);
		sp(217507, 329.28490f, 355.23140f, 96.09093f, (byte) 0, 2000);
		sp(217504, 328.90808f, 351.61840f, 96.09092f, (byte) 0, 2000);
	}
	
	private void startStage2Round3() {
		sendEventPacket(StageType.START_STAGE_2_ROUND_3, 2000);
		switch (Rnd.get(1, 2)) {
		    case 1:
				sp(217500, 332.24298f, 349.49286f, 96.090935f, (byte) 0, 2000); //Chieftain Kurka.
			break;
			case 2:
				sp(217509, 332.24298f, 349.49286f, 96.090935f, (byte) 0, 2000); //Chieftain Nuaka.
			break;
		}
	}
	
	private void startStage2Round4() {
		List<Npc> round = new ArrayList<Npc>();
		round.add(sp(217508, 334.06754f, 339.84393f, 96.09091f, (byte) 0));
		empyreanStage.add(new EmpyreanStage(round));
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				List<Npc> round1 = new ArrayList<Npc>();
				round1.add(sp(217506, 342.12405f, 364.4922f, 96.09093f, (byte) 0));
				round1.add(sp(217507, 344.4953f, 365.14444f, 96.09092f, (byte) 0));
				empyreanStage.add(new EmpyreanStage(round1));
			}
		}, 5000);
	}
	
	private void startStage2Round5() {
		sendEventPacket(StageType.START_STAGE_2_ROUND_5, 2000);
		switch (Rnd.get(1, 2)) {
		    case 1:
				sp(217501, 332.0035f, 349.55893f, 96.09093f, (byte) 0, 2000); //Grand Chieftain Saendukal.
			break;
			case 2:
				sp(217510, 332.0035f, 349.55893f, 96.09093f, (byte) 0, 2000); //Grand Chieftain Kasika.
			break;
		}
	}
	
	private void startStage4Round3() {
		sendEventPacket(StageType.START_STAGE_4_ROUND_3, 2000);
		sp(217563, 339.70975f, 333.54272f, 96.090904f, (byte) 20, 6000);
		sp(217564, 342.92892f, 333.43994f, 96.09092f, (byte) 18, 6000);
		sp(217565, 341.55396f, 330.70847f, 96.09093f, (byte) 23, 16000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				List<Npc> round = new ArrayList<Npc>();
				round.add(sp(217566, 362.87164f, 357.87164f, 96.091125f, (byte) 73));
				round.add(sp(217563, 359.1135f, 359.6953f, 96.091125f, (byte) 80));
				empyreanStage.add(new EmpyreanStage(round));
			}
		}, 43000);
	}
	
	private void rewardGroup() {
		for (Player p : instance.getPlayersInside()) {
			doReward(p);
		}
	}
	
	@Override
	public void doReward(Player player) {
		CruciblePlayerReward playerReward = getPlayerReward(player.getObjectId());
		float reward = 0.02f * playerReward.getPoints();
		if (playerReward.getPoints() != 0) {
			if (!playerReward.isRewarded()) {
				playerReward.setRewarded();
				playerReward.setInsignia((int) reward);
				ItemService.addItem(player, 186000169, (int) reward);
				ItemService.addItem(player, RndArray.get(crucibleRewardBox), 1);
				ItemService.addItem(player, RndArray.get(crucibleRewardKinah), 1);
			}
		} else {
			TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
		}
		PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(instanceReward, InstanceScoreType.END_PROGRESS));
	}
	
	private static final int[] crucibleRewardBox = {
		188052892, 188052893, 188052894, 188052895, 188052897
	};
	private static final int[] crucibleRewardKinah = {
		182006438, 182006439, 182006440, 182006441, 182006442, 182006443, 182006444
	};
	
	@Override
	public void onInstanceDestroy() {
		super.onInstanceDestroy();
		npcs.clear();
		empyreanStage.clear();
	}
	
	@Override
	public boolean onReviveEvent(final Player player) {
		super.onReviveEvent(player);
		moveToReadyRoom(player);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (player.getObjectId() == p.getObjectId()) {
					///You failed the training and have been sent to the Ready Room.
					PacketSendUtility.sendPacket(p, new S_MESSAGE_CODE(1400932));
				} else {
					///"Player Name" failed the training and has been sent to the Ready Room.
					PacketSendUtility.sendPacket(p, new S_MESSAGE_CODE(1400933, player.getName()));
				}
			}
		});
		return true;
	}
	
	private EmpyreanStage getEmpyreanStage(Npc npc) {
		for (EmpyreanStage es: empyreanStage) {
			if (es.npcs.contains(npc)) {
				return es;
			}
		}
		return null;
	}
	
	private boolean isSpawn(List<Integer> round) {
		for (Npc n: npcs) {
			if (round.contains(n.getNpcId())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onChangeStage(final StageType type) {
		switch (type) {
			case START_STAGE_1_ELEVATOR:
                sendEventPacket(type, 0);
				stage = 1;
                sendEventPacket(StageType.START_STAGE_1_ROUND_1, 2000);
				sp(799573, 384.51000f, 352.61078f, 96.74763f, (byte) 83);
                sp(217486, 327.73657f, 347.96228f, 96.09092f, (byte) 0, 2000);
                sp(217489, 327.81943f, 350.94800f, 96.09093f, (byte) 0, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(1), 4000);
					}
				});
            break;
			case START_STAGE_2_ELEVATOR:
				sendEventPacket(type, 0);
				stage = 2;
				despawnNpcs(instance.getNpcs(217756)); //Worthiness Ticket Box.
				sendEventPacket(StageType.START_STAGE_2_ROUND_1, 2000);
				sp(217503, 325.71194f, 352.81027f, 96.090920f, (byte) 0, 2000);
				sp(217502, 325.78696f, 346.07263f, 96.090904f, (byte) 0, 3000);
				sp(217504, 325.06122f, 349.47840f, 96.090904f, (byte) 0, 4000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(2), 4000);
					}
				});
			break;
			case START_STAGE_3_ELEVATOR:
				sendEventPacket(type, 0);
				stage = 3;
				despawnNpcs(instance.getNpcs(217737)); //King Saam.
				despawnNpcs(instance.getNpcs(799569));
				sendEventPacket(StageType.START_STAGE_3_ROUND_1, 2000);
				sp(217512, 344.23056f, 347.89594f, 96.09096f, (byte) 0, 3000);
				sp(217513, 341.09082f, 337.95187f, 96.09097f, (byte) 0, 3000);
				sp(217512, 342.06656f, 361.16135f, 96.090935f, (byte) 0, 3000);
				sp(217511, 356.75006f, 335.27487f, 96.09096f, (byte) 0, 3000);
				sp(217514, 345.4355f, 365.05215f, 96.09093f, (byte) 0, 3000);
				sp(217512, 352.8222f, 358.33463f, 96.09092f, (byte) 0, 3000);
				sp(217513, 342.32755f, 365.00473f, 96.09093f, (byte) 0, 3000);
				sp(217514, 356.19113f, 362.22543f, 96.090965f, (byte) 0, 3000);
				sp(217511, 344.25127f, 334.1194f, 96.090935f, (byte) 0, 3000);
				sp(217511, 344.07086f, 346.8839f, 96.09092f, (byte) 0, 3000);
				sp(217514, 334.01746f, 350.76382f, 96.090935f, (byte) 0, 3000);
				sp(217513, 344.49155f, 351.73932f, 96.09093f, (byte) 0, 3000);
				sp(217513, 353.0832f, 362.178f, 96.09092f, (byte) 0, 3000);
				sp(217511, 356.24454f, 358.34552f, 96.09103f, (byte) 0, 3000);
				sp(217512, 330.64853f, 346.87302f, 96.09091f, (byte) 0, 3000);
				sp(217512, 353.32773f, 335.26398f, 96.09092f, (byte) 0, 3000);
				sp(217514, 356.69666f, 339.1548f, 96.09103f, (byte) 0, 3000);
				sp(217511, 347.6529f, 347.90683f, 96.09098f, (byte) 0, 3000);
				sp(217514, 347.5995f, 351.78674f, 96.09099f, (byte) 0, 3000);
				sp(217512, 340.82983f, 334.1085f, 96.09093f, (byte) 0, 3000);
				sp(217514, 344.19876f, 337.9993f, 96.09094f, (byte) 0, 3000);
				sp(217513, 353.5887f, 339.10763f, 96.09092f, (byte) 0, 3000);
				sp(217511, 345.4889f, 361.17224f, 96.090935f, (byte) 0, 3000);
				sp(217513, 330.90952f, 350.7164f, 96.09093f, (byte) 0, 3000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(3), 4000);
					}
				});
			break;
			case START_STAGE_4_ELEVATOR:
				sendEventPacket(type, 0);
				despawnNpcs(instance.getNpcs(217735));
				sendEventPacket(StageType.START_STAGE_4_ROUND_1, 2000);
				stage = 4;
				sp(217557, 328.88104f, 349.55392f, 96.090904f, (byte) 0, 3000);
				sp(217559, 328.38922f, 342.39066f, 96.09091f, (byte) 5, 3000);
				sp(217557, 333.17947f, 336.4504f, 96.090904f, (byte) 8, 3000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(4), 4000);
					}
				});
			break;
			case START_STAGE_5:
				stage = 5;
				sp(205426, 1256.2872f, 834.28986f, 358.60565f, (byte) 103);
				sp(205332, 1260.1292f, 795.06964f, 358.60562f, (byte) 30, 10000);
				crucibleTeleport(1260.15f, 812.34f, 358.6056f, (byte) 90);
				sendEventPacket(type, 2000);
			break;
			//Stage 5 [Azoturan Version]
			case START_AZOTURAN_STAGE_5_ROUND_1:
				sendEventPacket(type, 2000);
				sp(217529, 1263.1987f, 778.4129f, 358.6056f, (byte) 30, 2000);
                sp(217529, 1260.1381f, 778.84644f, 358.60562f, (byte) 30, 2000);
                sp(217529, 1257.3065f, 778.35016f, 358.60562f, (byte) 30, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(5), 4000);
					}
				});
			break;
			//Stage 5 [Steel Rake Version]
			case START_STEEL_RAKE_STAGE_5_ROUND_1:
				sendEventPacket(type, 2000);
				sp(217549, 1263.1987f, 778.4129f, 358.6056f, (byte) 30, 2000);
                sp(217548, 1260.1381f, 778.84644f, 358.60562f, (byte) 30, 2000);
                sp(217547, 1257.3065f, 778.35016f, 358.60562f, (byte) 30, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(5), 4000);
					}
				});
			break;
			case START_STAGE_6:
				stage = 6;
				sp(205427, 1594.4756f, 145.26898f, 128.67778f, (byte) 16);
				sp(205333, 1629.7583f, 156.67245f, 126.00000f, (byte) 75, 10000);
				crucibleTeleport(1615.6274f, 142.8223f, 126.0000f, (byte) 15);
				sendEventPacket(type, 2000);
			break;
			case START_STAGE_6_ROUND_1:
				sendEventPacket(type, 2000);
				sp(217568, 1636.7102f, 166.87984f, 126f, (byte) 60, 2000);
                sp(217568, 1619.4432f, 153.83188f, 126f, (byte) 60, 2000);
                sp(217568, 1636.6416f, 164.15344f, 126f, (byte) 60, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(6), 4000);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						sp(217568, 1638.7107f, 165.40533f, 126f, (byte) 60);
						sp(217568, 1638.6783f, 162.67389f, 126f, (byte) 60);
						isDoneStage6Round1 = true;
					}
				}, 12000);
			break;
			case START_STAGE_7:
				stage = 7;
				sp(205428, 1820.39f, 800.81805f, 470.1394f, (byte) 86);
				sp(205334, 1781.6106f, 796.9224f, 469.35016f, (byte) 0, 10000);
				crucibleTeleport(1793.9233f, 796.92f, 469.36542f, (byte) 60);
				sendEventPacket(type, 2000);
			break;
			case START_STAGE_7_ROUND_1:
				sendEventPacket(type, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(7), 4000);
					}
				});
			break;
			case START_STAGE_8:
				stage = 8;
				sp(205429, 1784.7867f, 1726.0922f, 304.17697f, (byte) 51);
                sp(205335, 1771.407f, 1762.9862f, 303.6954f, (byte) 102, 10000);
                crucibleTeleport(1776.4169f, 1749.9952f, 303.69553f, (byte) 0);
				sendEventPacket(type, 2000);
			break;
			case START_STAGE_8_ROUND_1:
				sendEventPacket(type, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(8), 4000);
					}
				});
				switch (Rnd.get(1, 2)) {
				    case 1:
					    sp(217588, 1758.1841f, 1770.473f, 303.7f, (byte) 107, 2000); //Kromede The Corrupt.
					break;
					case 2:
					    sp(217589, 1758.1841f, 1770.473f, 303.7f, (byte) 107, 2000); //Vile Judge Kromede.
					break;
				}
			break;
			case START_STAGE_9:
				stage = 9;
				sp(205430, 1356.3973f, 1759.6832f, 319.625f, (byte) 83);
                sp(205336, 1324.5742f, 1739.683f, 316.4109f, (byte) 8, 10000);
                crucibleTeleport(1328.935f, 1742.0771f, 316.74188f, (byte) 0);
                sendEventPacket(type, 2000);
			break;
			case START_STAGE_9_ROUND_1:
				sendEventPacket(type, 2000);
				sp(217594, 1282.5399f, 1755.8711f, 317.4f, (byte) 105, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(9), 4000);
					}
				});
			break;
			case START_STAGE_10:
                stage = 10;
				sp(205431, 1755.709f, 1253.4136f, 394.2378f, (byte) 33);
                sp(205337, 1766.5986f, 1291.2572f, 394.23755f, (byte) 82, 10000);
                crucibleTeleport(1760.9441f, 1278.033f, 394.23764f, (byte) 0);
                sendEventPacket(type, 2000);
            break;
            case START_STAGE_10_ROUND_1:
                sendEventPacket(type, 2000);
                sp(217600, 1771.213f, 1302.0781f, 394.3f, (byte) 82, 2000);
                sp(217601, 1774.4563f, 1302.1516f, 394.3f, (byte) 82, 2000);
                sp(217602, 1765.1488f, 1305.1216f, 394.3f, (byte) 84, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///Round %0 begins!
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_START_ROUND_IDARENA(10), 4000);
					}
				});
            break;
		}
	}
	
	private void crucibleTeleport(float x, float y, float z, byte h) {
		for (Player player: instance.getPlayersInside()) {
			if (player.isOnline()) {
				TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
			}
		}
	}
	
	protected void crucibleStage1_4(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage5(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage6(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage7(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage8(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage9(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void crucibleStage10(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void readyRoomTeleport(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void moveToReadyRoom(Player player) {
		switch (stage) {
			case 1:
			case 2:
			case 3:
			case 4:
			    if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 381.41684f, 346.78162f, 96.74763f, (byte) 43);
				} else {
					readyRoomTeleport(player, 381.41684f, 346.78162f, 96.74763f, (byte) 43);
				}
			break;
			case 5:
				if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1260.9495f, 832.87317f, 358.60562f, (byte) 92);
				} else {
					readyRoomTeleport(player, 1260.9495f, 832.87317f, 358.60562f, (byte) 92);
				}
			break;
			case 6:
				if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1592.8813f, 149.78166f, 128.81355f, (byte) 117);
				} else {
					readyRoomTeleport(player, 1592.8813f, 149.78166f, 128.81355f, (byte) 117);
				}
			break;
			case 7:
				if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1820.8805f, 795.80914f, 470.18304f, (byte) 51);
				} else {
					readyRoomTeleport(player, 1820.8805f, 795.80914f, 470.18304f, (byte) 51);
				}
			break;
			case 8:
				if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1780.103f, 1723.458f, 304.039f, (byte) 53);
				} else {
					readyRoomTeleport(player, 1780.103f, 1723.458f, 304.039f, (byte) 53);
				}
			break;
			case 9:
				if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1359.5046f, 1751.7952f, 319.59406f, (byte) 30);
				} else {
					readyRoomTeleport(player, 1359.5046f, 1751.7952f, 319.59406f, (byte) 30);
				}
			break;
			case 10:
			    if (instance.getPlayersInside().size() < 2) {
					onStopTraining(player);
					readyRoomTeleport(player, 1755.709f, 1253.4136f, 394.2378f, (byte) 33);
				} else {
					readyRoomTeleport(player, 1755.709f, 1253.4136f, 394.2378f, (byte) 33);
				}
			break;
		}
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		CruciblePlayerReward reward = getPlayerReward(player.getObjectId());
		removeItems(player);
		if (reward != null) {
			reward.setPlayerLeave();
		}
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onExitInstance(Player player) {
		removeItems(player);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
		///"Player Name" dropped out of training and left the Crucible.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400962, player.getName()));
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
        storage.decreaseByItemId(186000124, storage.getItemCountByItemId(186000124));
		storage.decreaseByItemId(186000125, storage.getItemCountByItemId(186000125));
		storage.decreaseByItemId(186000134, storage.getItemCountByItemId(186000134));
	}
	
	@Override
	public void onStopTraining(Player player) {
		doReward(player);
	}
	
	private class EmpyreanStage {
		private List<Npc> npcs = new ArrayList<Npc>();
		
		public EmpyreanStage(List<Npc> npcs) {
			this.npcs = npcs;
		}
		
		private boolean containNpc() {
			for (Npc npc : npcs) {
				if (instance.getNpcs().contains(npc)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private void sendPacket(final int points, final int nameId) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					CruciblePlayerReward playerReward = getPlayerReward(player.getObjectId());
					if (nameId != 0) {
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400237, new DescriptionId(nameId * 2 + 1), points));
					} if (!playerReward.isRewarded()) {
						playerReward.addPoints(points);
					}
					PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(instanceReward));
				}
			}
		});
	}
	
	private void sendEventPacket(final StageType type, final int time) {
		this.stageType = type;
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new S_WORLD_SCENE_STATUS(2, type.getId(), type.getType()));
					}
				});
			}
		}, time);
	}
	
	private void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					npcs.add((Npc) spawn(npcId, x, y, z, h));
				}
			}
		}, time);
	}
	
	private Npc sp(int npcId ,float x, float y, float z, byte h) {
		Npc npc = null;
		if (!isInstanceDestroyed) {
			npc = (Npc) spawn(npcId, x, y, z, h);
			npcs.add(npc);
		}
		return npc;
	}
}