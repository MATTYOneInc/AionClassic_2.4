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
package quest.morheim;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _2039Allies_Among_Enemies extends QuestHandler
{
	private final static int questId = 2039;
	private final static int[] npcs = {204345, 204387, 204388, 204411, 204412, 204413};
	
	public _2039Allies_Among_Enemies() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204345) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204387) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_ACTION_1353: {
						if (var == 1) {
							playQuestMovie(env, 84);
							return sendQuestDialog(env, 1353);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						///Chieftain Chaikata, you can trusssst this perrrrson.
						NpcShoutsService.getInstance().sendMsg(npc, 1100711, npc.getObjectId(), 0, 1000);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204411) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1694);
						}
					} case STEP_TO_3: {
						qs.setQuestVarById(1, 1);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getSpawn().setWalkerId("df2_df2_npcpath_q2039a");
						WalkManager.startWalking((NpcAI2) npc.getAi2());
						npc.setState(1);
						PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
						///Hurry and escape!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100707, player.getObjectId(), 2));
						///Thank you, Daeva!
						NpcShoutsService.getInstance().sendMsg(npc, 1100708, npc.getObjectId(), 0, 1000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								npc.getController().onDelete();
								npc.getSpawn().setWalkerId(null);
								npc.getController().scheduleRespawn();
								WalkManager.stopWalking((NpcAI2) npc.getAi2());
							}
						}, 8000);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204412) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1779);
						}
					} case STEP_TO_4: {
						qs.setQuestVarById(2, 1);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getSpawn().setWalkerId("df2_df2_npcpath_q2039b");
						WalkManager.startWalking((NpcAI2) npc.getAi2());
						npc.setState(1);
						PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
						///Hurry and escape!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100707, player.getObjectId(), 2));
						///I'm finally frrrree!
						NpcShoutsService.getInstance().sendMsg(npc, 1100709, npc.getObjectId(), 0, 1000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								npc.getController().onDelete();
								npc.getSpawn().setWalkerId(null);
								npc.getController().scheduleRespawn();
								WalkManager.stopWalking((NpcAI2) npc.getAi2());
							}
						}, 8000);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204413) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1864);
						}
					} case STEP_TO_5: {
						qs.setQuestVarById(3, 1);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getSpawn().setWalkerId("df2_df2_npcpath_q2039c");
						WalkManager.startWalking((NpcAI2) npc.getAi2());
						npc.setState(1);
						PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
						///Hurry and escape!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100707, player.getObjectId(), 2));
						///Thank you. I'll take carrrre of the ressst.
						NpcShoutsService.getInstance().sendMsg(npc, 1100710, npc.getObjectId(), 0, 1000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								npc.getController().onDelete();
								npc.getSpawn().setWalkerId(null);
								npc.getController().scheduleRespawn();
								WalkManager.stopWalking((NpcAI2) npc.getAi2());
							}
						}, 8000);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204388) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}