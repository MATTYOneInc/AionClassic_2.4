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
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2345Orashunerk_Special_Order extends QuestHandler
{
	private final static int questId = 2345;
	private int rewardWay = 0;
	
	public _2345Orashunerk_Special_Order() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(798084).addOnQuestStart(questId);
		qe.registerQuestNpc(798084).addOnTalkEvent(questId);
		qe.registerQuestNpc(700238).addOnTalkEvent(questId);
		qe.registerQuestNpc(204339).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798084) {
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
		} else if (targetId == 700238) {
			if (env.getDialog() == QuestDialog.USE_OBJECT) {
				return closeDialogWindow(env);
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798084) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1353: {
						return sendQuestDialog(env, 1353);
					} case SELECT_ACTION_1438: {
						return sendQuestDialog(env, 1438);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 0, 1, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case STEP_TO_10: {
						changeQuestStep(env, 1, 10, false);
						giveQuestItem(env, 182204137, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_20: {
						changeQuestStep(env, 1, 20, false);
						giveQuestItem(env, 182204138, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204339) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					if (var == 10) {
						removeQuestItem(env, 182204137, 1);
						return sendQuestDialog(env, 1693);
					} else if (var == 20) {
						rewardWay = 1;
						removeQuestItem(env, 182204138, 1);
						return sendQuestDialog(env, 2034);
					}
				}
				return sendQuestEndDialog(env, rewardWay);
			}
		}
		return false;
	}
}