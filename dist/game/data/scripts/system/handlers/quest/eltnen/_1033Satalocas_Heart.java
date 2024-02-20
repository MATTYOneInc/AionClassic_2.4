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
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/** https://www.youtube.com/watch?v=VgO6TO7xKaA
/****/

public class _1033Satalocas_Heart extends QuestHandler
{
	private final static int questId = 1033;
	private final static int[] npcs = {203900, 203996};
	
	public _1033Satalocas_Heart() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
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
			if (targetId == 203900) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 178);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203996) {
				long drakeFangs = player.getInventory().getItemCountByItemId(182201019);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						} else if (var == 11) {
							return sendQuestDialog(env, 2034);
						}
					} case SELECT_ACTION_1695: {
						if (var == 1) {
							playQuestMovie(env, 42);
							return sendQuestDialog(env, 1695);
						}
					} case STEP_TO_3: {
						if (var == 1) {
							changeQuestStep(env, 1, 10, false);
							QuestService.questTimerStart(env, 180);
						} else if (drakeFangs == 0 && var == 10) {
							return closeDialogWindow(env);
						}
						return closeDialogWindow(env);
					} case SELECT_ACTION_2035: {
						if (drakeFangs == 0 && var == 11) {
							changeQuestStep(env, 11, 10, false);
							QuestService.questTimerStart(env, 180);
							return closeDialogWindow(env);
						} else if (drakeFangs >= 1 && drakeFangs < 3 && var == 11) {
							changeQuestStep(env, 11, 10, false);
							QuestService.questTimerStart(env, 180);
							removeQuestItem(env, 182201019, 10);
							return sendQuestDialog(env, 2035);
						} else if (drakeFangs >= 3 && drakeFangs < 5 && var == 11) {
							qs.setQuestVar(12);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182201019, 10);
							return sendQuestDialog(env, 2120);
						} else if (drakeFangs >= 5 && var == 11) {
							qs.setQuestVar(13);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182201019, 10);
							return sendQuestDialog(env, 2205);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203900 && qs.getQuestVarById(0) == 12) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			} else if (targetId == 203900 && qs.getQuestVarById(0) == 13) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2716);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 6);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 10) {
                qs.setQuestVar(11);
                updateQuestStatus(env);
                return true;
            }
        }
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 10 && var < 13) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}