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
package quest.craft;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _29038Master_Cooks_Potential extends QuestHandler
{
	private final static int questId = 29038;
	
	public _29038Master_Cooks_Potential() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnFailCraft(182207907, questId);
		qe.registerOnFailCraft(182207908, questId);
		qe.registerOnFailCraft(182207909, questId);
		qe.registerOnFailCraft(182207910, questId);
		qe.registerQuestNpc(204100).addOnQuestStart(questId);
		qe.registerQuestNpc(204100).addOnTalkEvent(questId);
		qe.registerQuestNpc(204101).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
		    if (targetId == 204100) {
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
			long kinah = player.getInventory().getKinah();
			if (targetId == 204101) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 1) {
							if (!player.getRecipeList().isRecipePresent(155007241)) {
								return sendQuestDialog(env, 4081);
							}
						} else if (var == 3) {
							return sendQuestDialog(env, 1352);
						} else if (var == 4) {
							if (!player.getRecipeList().isRecipePresent(155007242)) {
								return sendQuestDialog(env, 4166);
							}
						} else if (var == 6) {
							return sendQuestDialog(env, 1693);
						} else if (var == 7) {
							if (!player.getRecipeList().isRecipePresent(155007243)) {
								return sendQuestDialog(env, 4251);
							}
						} else if (var == 9) {
							return sendQuestDialog(env, 2034);
						} else if (var == 10) {
							if (!player.getRecipeList().isRecipePresent(155007244)) {
								return sendQuestDialog(env, 4336);
							}
						}
					} case STEP_TO_10: {
						if (kinah >= 6500) {
							if (var == 0) {
								giveQuestItem(env, 152207202, 1);
								changeQuestStep(env, 0, 2, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							} else if (var == 1) {
								giveQuestItem(env, 152207202, 1);
								changeQuestStep(env, 1, 2, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							}
						} else {
							return sendQuestDialog(env, 4400);
						}
					} case STEP_TO_20: {
						if (kinah >= 6500) {
							if (var == 3) {
								giveQuestItem(env, 152207203, 1);
								changeQuestStep(env, 3, 5, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							} else if (var == 4) {
								giveQuestItem(env, 152207203, 1);
								changeQuestStep(env, 4, 5, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							}
						} else {
							return sendQuestDialog(env, 4400);
						}
					} case STEP_TO_30: {
						if (kinah >= 6500) {
							if (var == 6) {
								giveQuestItem(env, 152207204, 1);
								changeQuestStep(env, 6, 8, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							} else if (var == 7) {
								giveQuestItem(env, 152207204, 1);
								changeQuestStep(env, 7, 8, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							}
						} else {
							return sendQuestDialog(env, 4400);
						}
					} case STEP_TO_40: {
						if (kinah >= 6500) {
							if (var == 9) {
								giveQuestItem(env, 152207205, 1);
								changeQuestStep(env, 9, 11, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							} else if (var == 10) {
								giveQuestItem(env, 152207205, 1);
								changeQuestStep(env, 10, 11, false);
								player.getInventory().decreaseKinah(6500);
								return closeDialogWindow(env);
							}
						} else {
							return sendQuestDialog(env, 4400);
						}
					}
				}
			} if (targetId == 204100) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1097);
						} else if (var == 5) {
							return sendQuestDialog(env, 1438);
						} else if (var == 8) {
							return sendQuestDialog(env, 1779);
						} else if (var == 11) {
							return sendQuestDialog(env, 2120);
						}
					} case STEP_TO_11: {
						return checkItemExistence(env, 2, 3, false, 182207907, 1, true, 1182, 2716, 0, 0);
					} case STEP_TO_21: {
						return checkItemExistence(env, 5, 6, false, 182207908, 1, true, 1523, 3057, 0, 0);
					} case STEP_TO_31: {
						return checkItemExistence(env, 8, 9, false, 182207909, 1, true, 1864, 3398, 0, 0);
					} case STEP_TO_41: {
						return checkItemExistence(env, 11, 11, true, 182207910, 1, true, 5, 3057, 0, 0);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204100) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onFailCraftEvent(QuestEnv env, int itemId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (itemId) {
				case 182207907: {
					changeQuestStep(env, 2, 1, false);
					return true;
				} case 182207908: {
					changeQuestStep(env, 5, 4, false);
					return true;
				} case 182207909: {
					changeQuestStep(env, 8, 7, false);
					return true;
				} case 182207910: {
					changeQuestStep(env, 11, 10, false);
					return true;
				}
			}
		}
		return false;
	}
}