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
package quest.ishalgen;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2136The_Lost_Axe extends QuestHandler
{
	private final static int questId = 2136;
	
	public _2136The_Lost_Axe() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182203130, questId);
		qe.registerQuestNpc(700146).addOnTalkEvent(questId);
		qe.registerQuestNpc(790009).addOnTalkEvent(questId);
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (targetId == 0) {
			switch (env.getDialog()) {
				case ASK_ACCEPTION: {
					return sendQuestDialog(env, 4);
				} case ACCEPT_QUEST: {
					return sendQuestStartDialog(env);
				} case REFUSE_QUEST: {
					return closeDialogWindow(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 700146) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 0) {
							playQuestMovie(env, 59);
							return useQuestObject(env, 0, 1, false, false);
						}
					}
				}
			} if (targetId == 790009) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						if (var == 1) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182203130, 1);
							return sendQuestDialog(env, 6);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							qs.setStatus(QuestStatus.REWARD);
							qs.setQuestVarById(1, 1);
							updateQuestStatus(env);
							removeQuestItem(env, 182203130, 1);
							return sendQuestDialog(env, 5);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790009) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}