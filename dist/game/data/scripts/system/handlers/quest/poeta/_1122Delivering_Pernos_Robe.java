/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique. If not, see <http://www.gnu.org/licenses/>.
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

public class _1122Delivering_Pernos_Robe extends QuestHandler
{
	private final static int questId = 1122;
	
	public _1122Delivering_Pernos_Robe() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203060).addOnQuestStart(questId);
		qe.registerQuestNpc(203060).addOnTalkEvent(questId);
		qe.registerQuestNpc(790001).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203060) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182200216, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			long scarFang = player.getInventory().getItemCountByItemId(182200218);
			long abexHorn = player.getInventory().getItemCountByItemId(182200219);
			long braxHoof = player.getInventory().getItemCountByItemId(182200220);
			if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						if (scarFang == 1) {
							qs.setQuestVar(1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200218, 1);
							removeQuestItem(env, 182200216, 1);
							return sendQuestDialog(env, 1523);
						} else {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_2: {
						if (abexHorn == 1) {
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200219, 1);
							removeQuestItem(env, 182200216, 1);
							return sendQuestDialog(env, 1438);
						} else {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_3: {
						if (braxHoof == 1) {
							qs.setQuestVar(3);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200220, 1);
							removeQuestItem(env, 182200216, 1);
							return sendQuestDialog(env, 1353);
						} else {
							return sendQuestDialog(env, 1608);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790001) {
				if (env.getDialog() == QuestDialog.USE_OBJECT || env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 4 + qs.getQuestVars().getQuestVars());
				} else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
					QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
					return closeDialogWindow(env);
				}
			}
		}
		return false;
	}
}