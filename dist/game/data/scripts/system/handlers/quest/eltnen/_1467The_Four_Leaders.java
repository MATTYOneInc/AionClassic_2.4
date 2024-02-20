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
package quest.eltnen;

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

public class _1467The_Four_Leaders extends QuestHandler
{
	private final static int questId = 1467;
	
	public _1467The_Four_Leaders() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204045).addOnQuestStart(questId);
		qe.registerQuestNpc(204045).addOnTalkEvent(questId);
		qe.registerQuestNpc(211696).addOnKillEvent(questId);
		qe.registerQuestNpc(211697).addOnKillEvent(questId);
		qe.registerQuestNpc(211698).addOnKillEvent(questId);
		qe.registerQuestNpc(211699).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204045) {
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
			if (targetId == 204045) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 0, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 0, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 0, 4, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204045) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						switch (qs.getQuestVarById(0)) {
							case 1: {
								return sendQuestDialog(env, 5);
							} case 2: {
								return sendQuestDialog(env, 6);
							} case 3: {
								return sendQuestDialog(env, 7);
							} case 4: {
								return sendQuestDialog(env, 8);
							}
						}
					} case SELECT_NO_REWARD: {
						QuestService.finishQuest(env, qs.getQuestVarById(0) - 1);
						return closeDialogWindow(env);
					}
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onKillEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				switch (targetId) {
					case 211696: { //Kamtak The Destroyer.
						changeQuestStep(env, 1, 1, true);
						return true;
					}
				}
			} else if (var == 2) {
				switch (targetId) {
					case 211697: { //Greatmage Teotem.
						changeQuestStep(env, 2, 2, true);
						return true;
					}
				}
			} else if (var == 3) {
				switch (targetId) {
					case 211698: { //Greathealer Quvak.
						changeQuestStep(env, 3, 3, true);
						return true;
					}
				}
			} else if (var == 4) {
				switch (targetId) {
					case 211699: { //Kantu.
						changeQuestStep(env, 4, 4, true);
						return true;
					}
				}
			}
		}
		return false;
    }
}