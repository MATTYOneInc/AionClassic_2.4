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
package quest.fenris_fang;

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

public class _4943Luck_And_Persistence extends QuestHandler
{
	private final static int questId = 4943;
	private final static int[] npcs = {204096, 204097, 204075, 204053};
	
	public _4943Luck_And_Persistence() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
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
			if (targetId == 204053) {
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
			long kinah = player.getInventory().getKinah();
			int var = qs.getQuestVarById(0);
			if (targetId == 204096) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 2, 3, false);
							giveQuestItem(env, 182207125, 1);
							removeQuestItem(env, 182207123, 1);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            } if (targetId == 204097) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1354: {
						if (kinah >= 3400000) {
							giveQuestItem(env, 182207123, 1);
							changeQuestStep(env, 1, 2, false);
							player.getInventory().decreaseKinah(3400000);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 1438);
						}
					}
                }
            } if (targetId == 204075) {
				long pureHolyWater = player.getInventory().getItemCountByItemId(186000084);
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case SET_REWARD: {
						if (pureHolyWater == 1) {
							removeQuestItem(env, 186000084, 1);
							return defaultCloseDialog(env, 3, 3, true, false, 0);
						} else {
							return sendQuestDialog(env, 2120);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204053) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182207125, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}