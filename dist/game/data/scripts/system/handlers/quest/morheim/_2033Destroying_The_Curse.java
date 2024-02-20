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
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.utils.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _2033Destroying_The_Curse extends QuestHandler
{
	private final static int questId = 2033;
	private final static int[] npcs = {204391, 790020, 204393, 204394, 204395, 204396, 204397, 204398};
	
	public _2033Destroying_The_Curse() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204007, questId);
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
			if (targetId == 204391) {
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
			} if (targetId == 790020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 2, 3, false, 10000, 10001);
					} case STEP_TO_4: {
						giveQuestItem(env, 182204007, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204393) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						playQuestMovie(env, 74);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204394 || targetId == 204395 || targetId == 204396 || targetId == 204397 || targetId == 204398) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getSpawn().setWalkerId("df2_df2_npcpath_q2033");
						WalkManager.startWalking((NpcAI2) npc.getAi2());
						npc.setState(1);
						PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
						///Follow me!
						NpcShoutsService.getInstance().sendMsg(npc, 1100702, npc.getObjectId(), 0, 0);
						///I'm so tired!
						NpcShoutsService.getInstance().sendMsg(npc, 1100703, npc.getObjectId(), 0, 30000);
						///My legs hurt. Let's rest for a while.
						NpcShoutsService.getInstance().sendMsg(npc, 1100721, npc.getObjectId(), 0, 60000);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 790020) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182204007, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 6) {
				if (player.isInsideZone(ZoneName.get("DF2_ITEMUSEAREA_Q2033"))) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 6, 6, true, 75));
				}
			}
		}
		return HandlerResult.SUCCESS;
	}
}