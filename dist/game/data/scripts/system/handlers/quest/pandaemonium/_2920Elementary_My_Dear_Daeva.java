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
package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _2920Elementary_My_Dear_Daeva extends QuestHandler
{
	private final static int questId = 2920;
	private int rewardWay = 0;
	
	public _2920Elementary_My_Dear_Daeva() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204141).addOnQuestStart(questId);
		qe.registerQuestNpc(204141).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204141) {
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
			if (targetId == 204141) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						return sendQuestDialog(env, 1352);
					} case STEP_TO_2: {
						return sendQuestDialog(env, 1693);
					} case STEP_TO_11: {
						rewardWay = 0;
						changeQuestStep(env, 0, 0, true);
						return sendQuestDialog(env, 5);
					} case STEP_TO_12: {
						rewardWay = 1;
						changeQuestStep(env, 0, 0, true);
						return sendQuestDialog(env, 6);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204141) {
				return sendQuestEndDialog(env, rewardWay);
			}
		}
		return false;
	}
}