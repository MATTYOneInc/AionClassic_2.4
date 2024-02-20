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

public class _1901Krallic_Language_Potion extends QuestHandler
{
	private final static int questId = 1901;
	
	public _1901Krallic_Language_Potion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203830).addOnQuestStart(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
		qe.registerQuestNpc(798026).addOnTalkEvent(questId);
		qe.registerQuestNpc(798025).addOnTalkEvent(questId);
		qe.registerQuestNpc(203131).addOnTalkEvent(questId);
		qe.registerQuestNpc(798003).addOnTalkEvent(questId);
		qe.registerQuestNpc(203864).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203830) {
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
			long kinah = player.getInventory().getKinah();
			if (targetId == 798026) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						} else if (var == 5) {
							return sendQuestDialog(env, 3057);
						}
					} case SELECT_ACTION_1438: {
					    if (kinah >= 10000) {
							player.getInventory().decreaseKinah(10000);
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 1438);
						} else {
							return sendQuestDialog(env, 1523);
						}
					} case STEP_TO_1: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						qs.setQuestVarById(0, var + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798025) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						} else if (var == 4) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_3: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182206000, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203131) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798003) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						giveQuestItem(env, 182206000, 1);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203864) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3398);
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