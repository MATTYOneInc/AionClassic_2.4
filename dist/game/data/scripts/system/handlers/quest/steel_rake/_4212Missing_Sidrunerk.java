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
package quest.steel_rake;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.task.QuestTasks;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _4212Missing_Sidrunerk extends QuestHandler
{
	private final static int questId = 4212;
	
	public _4212Missing_Sidrunerk() {
		super(questId);
	}
	
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerQuestNpc(204283).addOnQuestStart(questId);
		qe.registerQuestNpc(204283).addOnTalkEvent(questId);
		qe.registerQuestNpc(798065).addOnTalkEvent(questId);
		qe.registerQuestNpc(798058).addOnTalkEvent(questId);
		qe.registerQuestNpc(798337).addOnTalkEvent(questId);
		qe.registerQuestNpc(730208).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204283) { 
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798065) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					if (qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				} else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					return defaultCloseDialog(env, 0, 1);
				}
			} else if (targetId == 798058) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					if (qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1352);
					}
				} else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					return defaultCloseDialog(env, 1, 2);
				}
			} else if (targetId == 798337) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					if (qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 1693);
					}
				} else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					Npc npc = (Npc) env.getVisibleObject();
					npc.getSpawn().setWalkerId("4212");
					WalkManager.startWalking((NpcAI2) npc.getAi2());
					PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
					player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, npc, 505.69427f, 437.69382f, 885.1844f));
					return defaultCloseDialog(env, 2, 3);
				}
			} else if (targetId == 730208) {
				Npc npc = (Npc) env.getVisibleObject();
				npc.getController().delete();
				return true;
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798058) {
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
	public boolean onLogOutEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(4);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
			}
		}
		return false;
	}
}