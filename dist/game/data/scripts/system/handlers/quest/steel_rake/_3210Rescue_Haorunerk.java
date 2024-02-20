/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modif (y
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.steel_rake;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _3210Rescue_Haorunerk extends QuestHandler
{
	private final static int questId = 3210;
	
	private final static int[] IDShip_KrallKeeperKnmd_44_Ae = {215056};
	private final static int[] IDShip_ShulackEngineerNmd_45_Ah = {215080};
	
	public _3210Rescue_Haorunerk() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798318).addOnQuestStart(questId);
		qe.registerQuestNpc(798318).addOnTalkEvent(questId);
		qe.registerQuestNpc(798331).addOnTalkEvent(questId);
		qe.registerQuestNpc(798333).addOnTalkEvent(questId);
		for (int mob: IDShip_KrallKeeperKnmd_44_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDShip_ShulackEngineerNmd_45_Ah) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798318) {
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
			if (targetId == 798333) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798331) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 10002);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798331) {
				if (env.getDialog() == QuestDialog.SELECT_REWARD) {
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
		int[] IDShip_KrallKeeperKnmd_44_Ae = {215056};
		int[] IDShip_ShulackEngineerNmd_45_Ah = {215080};
		if (defaultOnKillEvent(env, IDShip_KrallKeeperKnmd_44_Ae, 0, 1, 1) ||
		    defaultOnKillEvent(env, IDShip_ShulackEngineerNmd_45_Ah, 0, 1, 2)) {
			return true;
		} else {
			return false;
		}
	}
}