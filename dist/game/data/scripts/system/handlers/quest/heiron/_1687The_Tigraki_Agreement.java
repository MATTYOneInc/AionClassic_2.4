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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1687The_Tigraki_Agreement extends QuestHandler
{
	private final static int questId = 1687;
	private int rewardId;
	
	public _1687The_Tigraki_Agreement() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204601).addOnQuestStart(questId);
		qe.registerQuestNpc(204601).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 204601) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204601) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
						    updateQuestStatus(env);
							return sendQuestDialog(env, 1352);
						} else {
							return sendQuestDialog(env, 1097);
						}
					} case SELECT_ACTION_1354: {
						rewardId = 0;
						return sendQuestDialog(env, 1354);
					} case SELECT_ACTION_1375: {
						rewardId = 1;
						return sendQuestDialog(env, 1375);
					} case SELECT_ACTION_1396: {
						rewardId = 2;
						return sendQuestDialog(env, 1396);
					} case STEP_TO_10: {
						return defaultCloseDialog(env, var, var, true, true, 0);
					} case STEP_TO_20: {
						return defaultCloseDialog(env, var, var, true, true, 1);
					} case STEP_TO_30: {
						return defaultCloseDialog(env, var, var, true, true, 2);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204601) {
				return sendQuestEndDialog(env, rewardId);
			}
		}
		return false;
	}
}