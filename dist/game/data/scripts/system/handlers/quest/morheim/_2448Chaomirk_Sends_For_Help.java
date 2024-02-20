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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _2448Chaomirk_Sends_For_Help extends QuestHandler
{
	private final static int questId = 2448;
	private int rewardWay = 0;
	
	public _2448Chaomirk_Sends_For_Help() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(798115).addOnQuestStart(questId);
		qe.registerQuestNpc(798115).addOnTalkEvent(questId);
		qe.registerQuestNpc(798080).addOnTalkEvent(questId);
		qe.registerQuestNpc(798079).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798115) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182204210, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798115) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1003);
						}
					} case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					} case SELECT_ACTION_1097: {
						return sendQuestDialog(env, 1097);
					} case STEP_TO_10: {
						playQuestMovie(env, 87);
						changeQuestStep(env, 0, 10, false);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_20: {
						rewardWay = 1;
						playQuestMovie(env, 87);
						changeQuestStep(env, 0, 20, false);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798080) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				}
			} else if (targetId == 798079) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1693);
				}
			}
			removeQuestItem(env, 182204210, 1);
			return sendQuestEndDialog(env, rewardWay);
		}
		return false;
	}
}