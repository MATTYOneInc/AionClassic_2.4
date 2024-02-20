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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1016Source_Of_The_Pollution extends QuestHandler
{
	private final static int questId = 1016;
	private final static int[] npcs = {203149, 203148, 203832, 203705, 203822, 203761, 203098, 203195};
	private final static int[] SlimeQ_18_An = {210318};
	
	public _1016Source_Of_The_Pollution() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: SlimeQ_18_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203149) {
				long slimeImmunityMedicine = player.getInventory().getItemCountByItemId(182200015);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						} else if (var == 8) {
							if (slimeImmunityMedicine == 0) {
								giveQuestItem(env, 182200015, 2);
								return sendQuestDialog(env, 3484);
							} else {
								return sendQuestDialog(env, 3569);
							}
						}
					} case SELECT_ACTION_1013: {
						return sendQuestDialog(env, 1013);
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						playQuestMovie(env, 28);
						giveQuestItem(env, 182200015, 2);
						removeQuestItem(env, 182200013, 1);
						removeQuestItem(env, 182200014, 1);
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203148) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182200017, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203832) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						giveQuestItem(env, 182200013, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203705) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203822) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						giveQuestItem(env, 182200018, 1);
						removeQuestItem(env, 182200017, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203761) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182200014, 1);
						removeQuestItem(env, 182200018, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203195) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						giveQuestItem(env, 182200016, 1);
						changeQuestStep(env, 9, 9, true);
						removeQuestItem(env, 182200015, 2);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203098) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 4080);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182200016, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (var == 8) {
				if (targetId == 210318) {
					return defaultOnKillEvent(env, SlimeQ_18_An, 8, 9);
				}
			}
		}
		return false;
	}
}