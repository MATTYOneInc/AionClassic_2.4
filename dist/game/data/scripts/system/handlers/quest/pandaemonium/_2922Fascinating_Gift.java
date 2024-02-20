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

public class _2922Fascinating_Gift extends QuestHandler
{
	private final static int questId = 2922;
	private int rewardWay = 0;

	public _2922Fascinating_Gift() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(204261).addOnQuestStart(questId);
		qe.registerQuestNpc(204261).addOnTalkEvent(questId);
		qe.registerQuestNpc(798058).addOnTalkEvent(questId);
		qe.registerQuestNpc(204108).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204261) {
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
			if (targetId == 204261) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1003);
						}
					} case STEP_TO_10: {
						changeQuestStep(env, 0, 10, false);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_20: {
						rewardWay = 1;
						changeQuestStep(env, 0, 20, false);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798058 && var == 10) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				}
			} else if (targetId == 204108 && var == 20) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1693);
				}
			}
			return sendQuestEndDialog(env, rewardWay);
		}
		return false;
	}
}