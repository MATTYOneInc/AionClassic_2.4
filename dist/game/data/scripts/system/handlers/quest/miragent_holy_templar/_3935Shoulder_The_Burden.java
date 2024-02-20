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
package quest.miragent_holy_templar;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _3935Shoulder_The_Burden extends QuestHandler
{
	private final static int questId = 3935;
	private final static int[] npcs = {203316, 203702, 203329, 203752, 203701};
	
	public _3935Shoulder_The_Burden() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203701).addOnQuestStart(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) {
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
			if (targetId == 203316) {
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
            } if (targetId == 203702) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203329) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 3, 4, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
                }
            } if (targetId == 203752) {
				long oathStone = player.getInventory().getItemCountByItemId(186000080);
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case SET_REWARD: {
						if (oathStone == 1) {
							removeQuestItem(env, 186000080, 1);
							return defaultCloseDialog(env, 4, 4, true, false, 0);
						} else {
							return sendQuestDialog(env, 2461);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203701) {
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