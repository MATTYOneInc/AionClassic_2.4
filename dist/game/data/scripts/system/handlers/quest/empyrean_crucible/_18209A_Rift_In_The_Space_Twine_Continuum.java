/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
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

public class _18209A_Rift_In_The_Space_Twine_Continuum extends QuestHandler
{
	private final static int questId = 18209;
	private final static int[] IDArena_Solo_S6_VanqJr_55_An = {217819};
	private final static int[] IDArena_Solo_H2_TempleD_Fi_55_Ae = {218200};
	
	public _18209A_Rift_In_The_Space_Twine_Continuum() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205309).addOnQuestStart(questId);
		qe.registerQuestNpc(205309).addOnTalkEvent(questId);
		for (int mob: IDArena_Solo_S6_VanqJr_55_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDArena_Solo_H2_TempleD_Fi_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 205309) {
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
            if (targetId == 205309) {
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
                    return defaultOnKillEvent(env, IDArena_Solo_H2_TempleD_Fi_55_Ae, var1, var1 + 1, 1);
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