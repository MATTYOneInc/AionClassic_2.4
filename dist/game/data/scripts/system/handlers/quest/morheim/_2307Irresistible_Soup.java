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

public class _2307Irresistible_Soup extends QuestHandler
{
	private final static int questId = 2307;
	
	public _2307Irresistible_Soup() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204378).addOnQuestStart(questId);
		qe.registerQuestNpc(204378).addOnTalkEvent(questId);
		qe.registerQuestNpc(204336).addOnTalkEvent(questId);
		qe.registerQuestNpc(700247).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204378) {
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
			if (targetId == 700247) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						return useQuestObject(env, 0, 1, false, true);
					}
                }
            } if (targetId == 204336) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1012: {
						changeQuestStep(env, 1, 1, true);
						removeQuestItem(env, 182204105, 1);
						return closeDialogWindow(env);
					} case SELECT_ACTION_1097: {
						changeQuestStep(env, 1, 1, true);
						removeQuestItem(env, 182204106, 1);
						return closeDialogWindow(env);
					} case SELECT_ACTION_1182: {
						changeQuestStep(env, 1, 1, true);
						removeQuestItem(env, 182204107, 1);
						return closeDialogWindow(env);
					} case SELECT_ACTION_1267: {
						changeQuestStep(env, 1, 1, true);
						removeQuestItem(env, 182204108, 1);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204378) {
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