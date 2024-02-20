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
package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1917A_Lingering_Mystery extends QuestHandler
{
	private final static int questId = 1917;
	private int rewardWay = 0;
	
	public _1917A_Lingering_Mystery() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203835).addOnQuestStart(questId);
		qe.registerQuestNpc(203835).addOnTalkEvent(questId);
		qe.registerQuestNpc(203075).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203835) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
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
			if (targetId == 203075) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203835) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						if (var == 1) {
							changeQuestStep(env, 1, 1, true);
							return sendQuestDialog(env, 5);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							changeQuestStep(env, 1, 2, true);
							return sendQuestDialog(env, 6);
						}
					} case STEP_TO_3: {
						if (var == 1) {
							changeQuestStep(env, 1, 3, true);
							return sendQuestDialog(env, 7);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203835) {
				if (var == 1) {
					rewardWay = 1;
				} else if (var == 2) {
					rewardWay = 2;
				} else if (var == 3) {
					rewardWay = 3;
				}
				return sendQuestEndDialog(env, rewardWay);
			}
		}
		return false;
	}
}