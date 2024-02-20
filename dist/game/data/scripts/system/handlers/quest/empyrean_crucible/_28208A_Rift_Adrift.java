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
package quest.empyrean_crucible;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _28208A_Rift_Adrift extends QuestHandler
{
	private final static int questId = 28208;
	private final static int[] IDArena_Solo_S6_VanqJr_55_An = {217819};
	private final static int[] IDArena_Solo_H2_TempleL_Fi_55_Ae = {218192};
	
	public _28208A_Rift_Adrift() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205320).addOnQuestStart(questId);
		qe.registerQuestNpc(205320).addOnTalkEvent(questId);
		qe.registerQuestNpc(205321).addOnTalkEvent(questId);
		for (int mob: IDArena_Solo_S6_VanqJr_55_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDArena_Solo_H2_TempleL_Fi_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205320) {
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
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205321) {
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
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 4) {
                    return defaultOnKillEvent(env, IDArena_Solo_S6_VanqJr_55_An, var1, var1 + 1, 1);
                } else if (var1 == 4) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 1) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, IDArena_Solo_H2_TempleL_Fi_55_Ae, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVarById(2, 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}