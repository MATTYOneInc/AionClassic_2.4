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
package quest.poeta;

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

public class _1004Neutralizing_Odium extends QuestHandler
{
	private final static int questId = 1004;
	private final static int[] npcs = {203082, 700030, 790001, 203067};
	
	public _1004Neutralizing_Odium() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 203082) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 5) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 19);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						if (var == 5) {
							qs.setStatus(QuestStatus.REWARD);
						    updateQuestStatus(env);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 1693);
						} else if (var == 11) {
							return sendQuestDialog(env, 1695);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						if (var == 11) {
							qs.setQuestVar(4);
							updateQuestStatus(env);
							giveQuestItem(env, 182200006, 1);
							removeQuestItem(env, 182200005, 1);
							return closeDialogWindow(env);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVar(11);
							updateQuestStatus(env);
							return sendQuestDialog(env, 1695);
						} else {
							return sendQuestDialog(env, 1779);
						}
					}
				}
			} if (targetId == 700030) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 1) {
							giveQuestItem(env, 182200005, 1);
							return useQuestObject(env, 1, 2, false, false);
						} else if (var == 4) {
							removeQuestItem(env, 182200006, 1);
							return useQuestObject(env, 4, 5, false, false);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203067) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}